package com.stafo.app.screens.payroll

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope


import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog

import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.databinding.ActivitySalarySlipBinding
import com.stafo.app.screens.payroll.dataClass.SalarySlipRequest
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.showCustomMonthYearPicker
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SalarySlipActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalarySlipBinding


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var mMonthOfSalary: String = ""
    private var slipUrl: String = ""
    private var mEMpId: Int = 0

    private lateinit var employeeListDialog: SearchableDialog

    private var mEmpList: List<GetEmployee>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalarySlipBinding.inflate(layoutInflater)
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
            settingsViewModel.getAllEmployeeList(this@SalarySlipActivity)

            tieEmployee.setOnClickListener {
                if (mMonthOfSalary.isBlank()) {
                    CustomToast(this@SalarySlipActivity, "Please select a month first")
                } else {
                    employeeListDialog.show()
                }
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            binding.tieMonth.setOnClickListener {
                showCustomMonthYearPicker(this@SalarySlipActivity) { formattedDate, displayDate ->
                    mMonthOfSalary = formattedDate
                    binding.tieMonth.setText(displayDate)
                }
            }

            rtlShare.setOnClickListener {
                if (slipUrl.isNotBlank()) {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, slipUrl)
                        type = "text/plain"
                    }

                    val chooser = Intent.createChooser(shareIntent, "Share File Link via")
                    startActivity(chooser)
                } else {
                    CustomToast(this@SalarySlipActivity, "Please generate salary slip first!")
                }

            }

            rtlDownload.setOnClickListener {
                if (slipUrl.isNotBlank()) {
                    val fileName = slipUrl.substringAfterLast("/")
                    val mimeType = getMimeType(slipUrl)
                    downloadFile(this@SalarySlipActivity, slipUrl, fileName, mimeType)
                } else {
                    CustomToast(this@SalarySlipActivity, "Please generate salary slip first!")
                }
            }


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

                    val result = getMonthAndYear(mMonthOfSalary)

                    if (result != null) {
                        val (month, year) = result
                        getEmployeeComId()?.let {
                            val request = SalarySlipRequest(
                                companyId = it,
                                employeeId = mEMpId.toString(),
                                month = month,
                                year = year
                            )
                            settingsViewModel.getSalarySlip(this@SalarySlipActivity, request)
                        }

                    }
                }

                dialog.dismiss()
            }
        })

        when (title) {
            "Employee List" -> employeeListDialog = dialog
        }
    }

    fun getMonthAndYear(dateString: String): Pair<String, String>? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val date = sdf.parse(dateString)

            val calendar = Calendar.getInstance()
            calendar.time = date!!

            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            Pair(month.toString().padStart(2, '0'), year.toString())
        } catch (e: Exception) {
            null
        }
    }


    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSalarySlipResponse.observe(this) {

            if (it.success) {

                CustomToast(this, it.message)
                slipUrl = it.data.download_url

                if (slipUrl.isNotBlank()) {
                    binding.pdfView.visibility = View.VISIBLE
                    binding.pdfView.initWithUrl(
                        url = slipUrl,
                        lifecycleCoroutineScope = lifecycleScope,
                        lifecycle = lifecycle,
                    )
                } else {
                    binding.pdfView.visibility = View.GONE
                    Log.e("PDF", "Slip URL is null or empty")
                }

            } else CustomToast(this, it.message)


        }

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
    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun getMimeType(url: String): String {
        return when {
            url.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            url.endsWith(".xls", ignoreCase = true) || url.endsWith(
                ".xlsx", ignoreCase = true
            ) -> "application/vnd.ms-excel"

            else -> "*/*"
        }
    }

    private fun downloadFile(context: Context, url: String, fileName: String, mimeType: String) {
        val request = DownloadManager.Request(Uri.parse(url)).setTitle("Downloading $fileName")
            .setDescription("Please wait...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType(mimeType).setAllowedOverMetered(true).setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        CustomToast(this, "Download started...")
    }
}