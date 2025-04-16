package com.stafo.app.screens.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.base.model.ProfileType
import com.stafo.app.databinding.ActivityEmplyeeyerProfileBinding
import com.stafo.app.screens.EmpProfileActivity
import com.stafo.app.screens.emp.EmpLeaveActivity
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.emp.EmployeeViewDocumentActivity
import com.stafo.app.screens.emp.HelpSupportActivity
import com.stafo.app.screens.settings.AddDepartmentActivity
import com.stafo.app.screens.settings.AddShiftActivity
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.FeedbackActivity
import com.stafo.app.screens.settings.GenerateQRActivity
import com.stafo.app.screens.settings.HolidayActivity
import com.stafo.app.screens.settings.LeaveManagementActivity
import com.stafo.app.screens.settings.PolicyActivity
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.ViewAllEmployeeActivity
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import com.stafo.app.utils.getCompanyDetails
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.stafo.app.screens.reports.ReportsActivity
import com.stafo.app.screens.settings.AssignBranchActivity
import com.stafo.app.screens.settings.UploadSelfieAttendanceActivity
import java.io.File

class EmplyeeyerProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEmplyeeyerProfileBinding
    private var profileImage: File? = null

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmplyeeyerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        setupView(binding)
        setOnClickEvents(binding)
        observeViewModel()
    }

    private fun setupView(binding: ActivityEmplyeeyerProfileBinding) {
        binding.apply {
            Log.e("TAG", "setupView: ${Gson().toJson(getEmployeeDetails())}")
            if (getIsCOMPANYLogin(this@EmplyeeyerProfile) == true) {

                settingsViewModel.getCompanyDetails(this@EmplyeeyerProfile)

                tvHeaderEmpName.text = getCompanyDetails()?.companyName ?: "Guest"
                tvHeaderEmpEmail.text = getCompanyDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.VISIBLE
                binding.llEmployerProfile.visibility = View.GONE

            } else {

                settingsViewModel.fetchEmployeeDetails(
                    this@EmplyeeyerProfile,
                    getEmployeeDetails()?.id.toString()
                )

                tvHeaderEmpName.text = getEmployeeDetails()?.name ?: "Guest"
                tvHeaderEmpEmail.text = getEmployeeDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.GONE
                binding.llEmployerProfile.visibility = View.VISIBLE

            }
        }
    }

    override fun onResume() {
        if (getIsCOMPANYLogin(this) == true) {
            settingsViewModel.getCompanyDetails(this@EmplyeeyerProfile)
        } else {
            settingsViewModel.fetchEmployeeDetails(
                this@EmplyeeyerProfile,
                getEmployeeDetails()?.id.toString()
            )

        }
        super.onResume()

    }


    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeUploadImageResponse.observe(this) {
            if (it.status) {

                settingsViewModel.fetchEmployeeDetails(
                    this@EmplyeeyerProfile,
                    getEmployeeDetails()?.id.toString()
                )

                CustomToast(this, it.message)

            } else {
                CustomToast(this, it.message)
            }


        }

        settingsViewModel.mFetchEmployeeDetailsResponse.observe(this) {
            if (it.status) {
                if (!it.imageUrl.isNullOrEmpty()) {
                    val imageUrl = it.imageUrl

                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.ivHeaderProfilePic)
                }

            } else {
                CustomToast(this, it.message)
            }


        }

        // company profile

        settingsViewModel.mCompanyProfileResponse.observe(this) {
            if (it.status) {

                it.data?.companyLogo?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.ivHeaderProfilePic)
                } ?: run {

                }


            } else {
                CustomToast(this, it.message)
            }

        }

        settingsViewModel.mCompanyUploadImageResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                settingsViewModel.getCompanyDetails(this@EmplyeeyerProfile)
            } else {
                CustomToast(this, it.message)
            }

        }

        settingsViewModel.mDeleteCompanyResponse.observe(this) {
            if (it.status) {
                doLogout(this)
                CustomToast(this, it.message)
                finishAffinity()
            } else {
                CustomToast(this, it.message)
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

    private fun setOnClickEvents(binding: ActivityEmplyeeyerProfileBinding) {


        binding?.ivChangePicture?.setOnClickListener {
            openPicker(1101)
        }

        binding?.expandableAccountSetting?.setOnClickListener {
            binding.expandableAccountSetting.toggleLayout()

        }

        binding?.expandableLeaveManagement?.setOnClickListener {
            binding.expandableLeaveManagement.toggleLayout()
        }

        binding?.expandableAttandancenManagement?.setOnClickListener {
            binding.expandableAttandancenManagement.toggleLayout()
        }

        binding?.expandableOtherManagement?.setOnClickListener {
            binding.expandableOtherManagement.toggleLayout()
        }

        val tvReportsSettings = binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_reports_settings)

        tvReportsSettings?.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
        }


        val qrCodeSettings = binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_generate_qr_settings)

        qrCodeSettings?.setOnClickListener {
            startActivity(Intent(this, GenerateQRActivity::class.java))
        }

        val departmentSettings = binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_department_settings)

        departmentSettings?.setOnClickListener {
            startActivity(Intent(this, AddDepartmentActivity::class.java))
        }


        binding.tvCompanyFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }


        val holidaySettings =
            binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_holiday_settings)

        holidaySettings?.setOnClickListener {
            startActivity(Intent(this, HolidayActivity::class.java))
        }

        val branchSettings =
            binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_branch_settings)

        branchSettings?.setOnClickListener {

            startActivity(Intent(this, BranchActivity::class.java))

        }

        val setAttendanceSetting = binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_set_attendance_settings)
        val assignBranchSettings = binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_assign_branch_settings)
        val assignDepartmentSettings = binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_assign_department_settings)

        assignBranchSettings?.setOnClickListener {
            startActivity(
                Intent(
                    this@EmplyeeyerProfile,
                    AssignBranchActivity::class.java
                ).apply {
                    putExtra("Assign_Type", "branch")
                })
        }

        assignDepartmentSettings?.setOnClickListener {
            startActivity(
                Intent(
                    this@EmplyeeyerProfile,
                    AssignBranchActivity::class.java
                ).apply {
                    putExtra("Assign_Type", "department")
                })
        }


        setAttendanceSetting?.setOnClickListener {


            startActivity(
                Intent(
                    this@EmplyeeyerProfile,
                    ViewAllEmployeeActivity::class.java
                ).apply {
                    putExtra("FROM", "SetAttendance")
                }
            )


        }


        val shiftSettings =
            binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_shift_settings)

        shiftSettings?.setOnClickListener {

            startActivity(Intent(this, AddShiftActivity::class.java))

        }


        val officePolicy =
            binding?.expandableLeaveManagement?.findViewById<AppCompatTextView>(R.id.tv_office_policy)

        officePolicy?.setOnClickListener {
            startActivity(Intent(this, PolicyActivity::class.java))
        }


        val requestLeave =
            binding?.expandableLeaveManagement?.findViewById<AppCompatTextView>(R.id.tv_leaves_management)

        requestLeave?.setOnClickListener {
            startActivity(Intent(this, LeaveManagementActivity::class.java))
        }

        binding.expandableOtherManagement.findViewById<AppCompatTextView>(R.id.tv_need_help_company)
            .setOnClickListener {
                startActivity(Intent(this, HelpSupportActivity::class.java))
            }


        // delete company account
        binding.expandableOtherManagement.findViewById<AppCompatTextView>(R.id.tv_delete_account)
            .setOnClickListener {
                showDialog()
            }




        // for employee

        binding?.tvLeave?.setOnClickListener {
            startActivity(Intent(this, EmpLeaveActivity::class.java))
        }

        binding.tvEmpFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }


        binding?.tvAttendance?.setOnClickListener {
            startActivity(Intent(this, EmployeeAttendanceRecordActivity::class.java))
        }
        binding?.expandableProfile?.setOnClickListener {
            binding.expandableProfile.toggleLayout()

        }

        binding?.tvPolicy?.setOnClickListener {
            startActivity(Intent(this, PolicyActivity::class.java))
        }

        binding.expandableOtherManagement.findViewById<AppCompatTextView>(R.id.tv_logout)
            .setOnClickListener {
                doLogout(this)
            }

        val basicProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_basic)

        basicProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.BASIC.name)
            startActivity(intent)
        }

        val professionalProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_professional)
        professionalProfile?.setOnClickListener {
            val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
            intent.putExtra("PROFILE_TYPE", ProfileType.PROFESSIONAL.name)
            startActivity(intent)
        }
    /*    val educationalProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_educational)
        educationalProfile?.setOnClickListener {

            CustomToast(this, "Working is progress")
            *//*  val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
              intent.putExtra("PROFILE_TYPE", ProfileType.EDUCATION.name)
              startActivity(intent)*//*
        }*/

        val documentProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_documents)
        documentProfile?.setOnClickListener {
            startActivity(Intent(this, EmployeeViewDocumentActivity::class.java))
        }

        binding.tvEmpLogout.setOnClickListener {
            doLogout(this)
        }

        binding.tvHelpSupport.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }
    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(this@EmplyeeyerProfile)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? You want to delete your account!")

        builder.setPositiveButton("Yes") { dialog, _ ->
            settingsViewModel.deleteAccount(this@EmplyeeyerProfile)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openPicker(req: Int) {

        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .start(req)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!

            val file = getFileFromUri(uri)

            if (file != null) {
                when (requestCode) {
                    1101 -> {
                        profileImage = file


                        if (getIsCOMPANYLogin(this) == true) {

                            settingsViewModel.changeCompanyProfileImage(this, file)
                        } else {

                            val id = getEmployeeDetails()?.id

                            id?.let {
                                settingsViewModel.changeEmpProfileImage(this, it, file)
                            } ?: Log.e("res", "Employee ID is null")
                        }


                    }

                }
            } else {
                CustomToast(this, "File selection failed")

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CustomToast(this, ImagePicker.getError(data))

        } else {
            // CustomToast(this, "Task Cancelled")

        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val fileName = getFileName(uri) ?: return null
        val file = File(cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null

        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
            }
        }

        if (name.isNullOrEmpty()) {
            name = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) {
                    path.substring(cut + 1)
                } else {
                    path
                }
            }
        }

        return name ?: "unknown_file"
    }


}