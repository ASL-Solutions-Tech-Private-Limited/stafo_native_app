package com.stafo.app.screens.rank

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.databinding.ActivityRankListBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.performance.EmpPerformanceAddActivity
import com.stafo.app.screens.performance.adapter.DynamicPerformanceAdapter
import com.stafo.app.screens.performance.dataClass.PerformanceAddRequest
import com.stafo.app.screens.performance.dataClass.PerformanceInput
import com.stafo.app.screens.rank.adapter.AdapterPoints
import com.stafo.app.screens.rank.adapter.RankListEmpAdapter
import com.stafo.app.screens.rank.dataClass.PerformanceRecord
import com.stafo.app.screens.rank.dataClass.PointsRequest
import com.stafo.app.screens.rank.dataClass.RankListRequest
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SalaryGeneratedRequest
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.showCustomMonthYearPicker

class RankListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankListBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private var mEmpList: List<GetEmployee>? = ArrayList()
    private lateinit var employeeListDialog: SearchableDialog

    private var mSelectMonth: String = ""
    private var mEMpId: Int = 0

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var rvAdapter: AdapterPoints
    private var list: List<PerformanceRecord> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRankListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()

    }

    private fun onClickListener() {


        binding.apply {

            getEmployeeComId()?.let {

                val request = ChatRequest(
                    company_id = it
                )

                billPaymentsViewModel.getPerformanceTypeList(
                    this@RankListActivity, request
                )
            }

            settingsViewModel.getAllEmployeeList(this@RankListActivity)

            tieEmployee.setOnClickListener {
                if (mSelectMonth.isBlank()) {
                    CustomToast(this@RankListActivity, "Please select a month first")
                } else {
                    employeeListDialog.show()
                }
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            llcAdd.setOnClickListener {
                startActivity(Intent(this@RankListActivity, EmpPerformanceAddActivity::class.java))
                overridePendingTransition(
                    com.stafo.app.R.anim.slide_from_right, com.stafo.app.R.anim.slide_to_left
                )
            }




            tieMonth.setOnClickListener {
                showCustomMonthYearPicker(this@RankListActivity) { formattedDate, displayDate ->
                    mSelectMonth = formattedDate
                    binding.tieMonth.setText(displayDate)

                    if (mEMpId!=0){
                        val (year, month) = mSelectMonth.split("-")

                        val request = RankListRequest(
                            employee_id = mEMpId.toString(), month = month, year = year
                        )
                        billPaymentsViewModel.viewRankList(this@RankListActivity, request)
                    }


                    Log.d("date", "$mSelectMonth")
                }
            }


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    mEmpList = it.data
                    binding.let { it1 ->
                        setupSearchableDialog(
                            mEmpList, "Employee List", it1.tieEmployee
                        )
                    }


                }


            }
        }


        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mRankListResponse.observe(this) {

            if (it.success) {

                if (!it.ranklist.isNullOrEmpty()) {

                    binding.rvRankList.visibility = View.VISIBLE
                    binding.llcMsg.visibility = View.GONE


                    val rvAdapter = RankListEmpAdapter(it.ranklist, this)

                    val layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvRankList.layoutManager = layoutManager
                    binding.rvRankList.adapter = rvAdapter


                } else {
                    binding.rvRankList.visibility = View.GONE
                    binding.llcMsg.visibility = View.VISIBLE
                }

            } else {
                binding.rvRankList.visibility = View.GONE
                binding.llcMsg.visibility = View.VISIBLE
            }


        }

        billPaymentsViewModel.mPointsResponse.observe(this) {

            if (it.success) {

                if (!it.data.isNullOrEmpty()) {
                    list = it.data
                    showPointsDetailsBottomSheet()


                } else CustomToast(this, "No Data Available")

            } else CustomToast(this, it.message)


        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun setupSearchableDialog(
        dataList: List<Any>?, title: String, field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is GetEmployee -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is GetEmployee -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Employee List") {

                    mEMpId = searchListItem.id

                    val (year, month) = mSelectMonth.split("-")

                    val request = RankListRequest(
                        employee_id = mEMpId.toString(), month = month, year = year
                    )
                    billPaymentsViewModel.viewRankList(this@RankListActivity, request)

                }

                dialog.dismiss()
            }
        })

        when (title) {
            "Employee List" -> employeeListDialog = dialog
        }
    }

    fun showPointsDetails() {
        val (year, month) = mSelectMonth.split("-")

        val request = PointsRequest(
            employee_id = mEMpId.toString(), month = month, year = year
        )
        billPaymentsViewModel.viewPointsDetails(this@RankListActivity, request)
    }

    @SuppressLint("MissingInflatedId")
    fun showPointsDetailsBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.points_details_bottom_sheet_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)


        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        val rvListPoint = view.findViewById<RecyclerView>(R.id.rv_p_points)
        rvAdapter = AdapterPoints(list, this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvListPoint.layoutManager = layoutManager
        rvListPoint.adapter = rvAdapter


        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }





        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }


}