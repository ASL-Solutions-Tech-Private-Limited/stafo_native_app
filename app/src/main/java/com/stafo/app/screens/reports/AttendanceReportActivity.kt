package com.stafo.app.screens.reports

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterAllEmpList
import com.stafo.app.base.adapter.AdapterDownloadReports
import com.stafo.app.databinding.ActivityAttendanceReportBinding
import com.stafo.app.screens.settings.AddBranchActivity
import com.stafo.app.screens.settings.AddDepartmentActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.DataBranch
import com.stafo.app.screens.settings.dataClass.DataDepartment
import com.stafo.app.screens.settings.dataClass.ReportsEmployeeListRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class AttendanceReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceReportBinding


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private var mDateOfReports: String = ""
    private var mEndDateOfReports: String = ""
    private var selectBranch: Int = 0
    private var selectDepartment: Int = 0
    private var selectedFormat: String = "pdf"
    private var selectedReportsType: String = "Attendance Reports"
    private var getReportType: String = ""

    private lateinit var branchDialog: SearchableDialog
    private lateinit var departmentDialog: SearchableDialog

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAttendanceReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        getReportType = intent.getStringExtra("reports_type") ?: ""
        onClickListener()

        observeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickListener() {
        binding.apply {


            if (getReportType == "emp_reports") {
                title.text = "Employee List Reports"
                rlSpnAttendanceReportsType.visibility = View.GONE

            } else {
                title.text = "Attendance Summary Reports"
                rlSpnAttendanceReportsType.visibility = View.VISIBLE
                initAttendanceReports()
            }



            imageBack.setOnClickListener {
                onBackPressed()
            }

            rgFormat.setOnCheckedChangeListener { group, checkedId ->
                val radioButtonCSV = findViewById<RadioButton>(R.id.csv)
                val radioButtonXLS = findViewById<RadioButton>(R.id.xsl)
                val radioButtonPDF = findViewById<RadioButton>(R.id.pdf)

                val allRadioButtons = listOf(radioButtonCSV, radioButtonXLS, radioButtonPDF)

                allRadioButtons.forEach { radioButton ->
                    radioButton.setTextColor(resources.getColor(R.color.black))
                    radioButton.compoundDrawables[0]?.setColorFilter(
                        resources.getColor(R.color.black),
                        PorterDuff.Mode.SRC_IN
                    )
                }


                val selectedRadioButton = findViewById<RadioButton>(checkedId)

                selectedRadioButton.setTextColor(resources.getColor(R.color.white))
                selectedRadioButton.compoundDrawables[0]?.setColorFilter(
                    resources.getColor(R.color.white),
                    PorterDuff.Mode.SRC_IN
                )

                val getSelectValue = selectedRadioButton.text.toString().lowercase()
                selectedFormat = if (getSelectValue == "xls") {
                    "excel"
                } else {
                    selectedRadioButton.text.toString().lowercase()
                }


            }


            tieBranch.setOnClickListener {

                if (!mBranchList.isNullOrEmpty()) {
                    branchDialog.show()
                } else {
                    CustomToast(this@AttendanceReportActivity, "Please first add branch!")
                }

            }
            tieDepartment.setOnClickListener {

                if (!mDepartmentList.isNullOrEmpty()) {
                    departmentDialog.show()
                } else {
                    CustomToast(this@AttendanceReportActivity, "Please first add department!")
                }

            }




            binding.tieDateReports.setOnClickListener {
                showDatePicker(binding.tieDateReports, "start")
            }

            binding.tieEndDateReports.setOnClickListener {
                showDatePicker(binding.tieEndDateReports, "end")
            }




            getEmployeeComId()?.let {
                settingsViewModel.getBranchList(
                    this@AttendanceReportActivity,
                    it
                )
            }

            getEmployeeComId()?.let {
                settingsViewModel.getDepartmentList(
                    this@AttendanceReportActivity,
                    it
                )
            }



            btnReports.setOnClickListener {


                if (selectedFormat.isNullOrEmpty()) {
                    CustomToast(this@AttendanceReportActivity, "Please select a report format.")
                    return@setOnClickListener
                }

                if (mDateOfReports.isNullOrEmpty()) {
                    CustomToast(this@AttendanceReportActivity, "Please select start date.")
                    return@setOnClickListener
                }

                if (mEndDateOfReports.isNullOrEmpty()) {
                    CustomToast(this@AttendanceReportActivity, "Please select end date.")
                    return@setOnClickListener
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    val startDate = dateFormat.parse(mDateOfReports)
                    val endDate = dateFormat.parse(mEndDateOfReports)

                    if (startDate != null && endDate != null && endDate.before(startDate)) {
                        CustomToast(this@AttendanceReportActivity, "End date cannot be earlier than start date.")
                        return@setOnClickListener
                    }
                } catch (e: ParseException) {
                    CustomToast(this@AttendanceReportActivity, "Invalid date format.")
                    return@setOnClickListener
                }


                if (getReportType == "emp_reports") {
                    getEmployeeComId()?.let { companyId ->
                        val request = ReportsEmployeeListRequest(
                            start_date = mDateOfReports,
                            end_date = mEndDateOfReports,
                            format = selectedFormat,
                            company_id = companyId,
                            department = selectDepartment,
                            branch = selectBranch
                        )

                        settingsViewModel.reportsEmployeeList(
                            this@AttendanceReportActivity,
                            request
                        )

                    } ?: run {
                        CustomToast(this@AttendanceReportActivity, "Company ID not found.")
                    }

                } else {
                    if (selectedReportsType == "Attendance Reports") {


                        getEmployeeComId()?.let { companyId ->
                            val request = ReportsEmployeeListRequest(
                                start_date = mDateOfReports,
                                end_date = mEndDateOfReports,
                                format = selectedFormat,
                                company_id = companyId,
                                department = selectDepartment,
                                branch = selectBranch
                            )

                            settingsViewModel.reportsAllEmployeeAttendance(
                                this@AttendanceReportActivity,
                                request
                            )

                        } ?: run {
                            CustomToast(this@AttendanceReportActivity, "Company ID not found.")
                        }
                    } else {
                        getEmployeeComId()?.let { companyId ->
                            val request = ReportsEmployeeListRequest(
                                start_date = mDateOfReports,
                                end_date = mEndDateOfReports,
                                format = selectedFormat,
                                company_id = companyId,
                                department = selectDepartment,
                                branch = selectBranch
                            )

                            settingsViewModel.reportsAllEmployeeLeave(
                                this@AttendanceReportActivity,
                                request
                            )

                        } ?: run {
                            CustomToast(this@AttendanceReportActivity, "Company ID not found.")
                        }
                    }
                }


            }


        }


    }

    private fun showDatePicker(view: TextInputEditText?, fieldType: String) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                val displayFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDisplayDate = displayFormat.format(selectedDate.time)

                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedApiDate = apiFormat.format(selectedDate.time)

                view?.setText(formattedDisplayDate)
                when (fieldType) {
                    "start" -> mDateOfReports = formattedApiDate
                    "end" -> mEndDateOfReports = formattedApiDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }




        settingsViewModel.mBranchListResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                mBranchList = it.data
                setupSearchableDialog(
                    mBranchList,
                    "Branch",
                    binding.tieBranch
                )
            }

        }




        settingsViewModel.mDepartmentListResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                mDepartmentList = it.data
                setupSearchableDialog(
                    mDepartmentList,
                    "Department",
                    binding.tieDepartment
                )
            }

        }
        settingsViewModel.mReportsEmployeeListResponse.observe(this) {

            if (it.success) {
                val fileUrl = it.download_url

                if (!fileUrl.isNullOrEmpty()) {
                    val fileName = fileUrl.substringAfterLast("/")
                    val mimeType = getMimeType(fileUrl)

                    downloadFile(this, fileUrl, fileName, mimeType)
                } else {
                    CustomToast(this, "Download URL is missing.")
                }
            } else {
                CustomToast(this, it.message)
            }

        }


    }

    fun getMimeType(url: String): String {
        return when {
            url.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            url.endsWith(".xls", ignoreCase = true) || url.endsWith(
                ".xlsx",
                ignoreCase = true
            ) -> "application/vnd.ms-excel"

            else -> "*/*"
        }
    }

    fun downloadFile(context: Context, url: String, fileName: String, mimeType: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading $fileName")
            .setDescription("Please wait...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType(mimeType)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        CustomToast(this, "Download started...")
    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataBranch -> it.branch_name
                is DataDepartment -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataBranch -> it.id
                is DataDepartment -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Branch") {
                    selectBranch = searchListItem.id
                } else if (title == "Department") {
                    selectDepartment = searchListItem.id
                }

                dialog.dismiss()


            }
        })
        when (title) {
            "Branch" -> branchDialog = dialog
            "Department" -> departmentDialog = dialog
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }

    private fun initAttendanceReports() {
        val marital = resources.getStringArray(R.array.reports_type)
        val adapterMarital = ArrayAdapter(this, R.layout.custom_spinner_item, marital)
        binding.spinnerAttendanceReports.setAdapter(adapterMarital)

        binding.spinnerAttendanceReports.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    selectedReportsType = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
    }
}