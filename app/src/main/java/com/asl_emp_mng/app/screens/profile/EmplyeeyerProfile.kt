package com.asl_emp_mng.app.screens.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityEmplyeeyerProfileBinding
import com.asl_emp_mng.app.screens.EmpProfileActivity
import com.asl_emp_mng.app.screens.emp.EmpLeaveActivity
import com.asl_emp_mng.app.screens.emp.EmployeeAttendance
import com.asl_emp_mng.app.screens.emp.EmployeeAttendanceRecordActivity
import com.asl_emp_mng.app.screens.settings.AddDepartmentActivity
import com.asl_emp_mng.app.screens.settings.AddShiftActivity
import com.asl_emp_mng.app.screens.settings.BranchActivity
import com.asl_emp_mng.app.screens.settings.HolidayActivity
import com.asl_emp_mng.app.screens.settings.LeaveManagementActivity
import com.asl_emp_mng.app.screens.settings.PolicyActivity
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.doLogout
import com.asl_emp_mng.app.utils.getCompanyDetails
import com.asl_emp_mng.app.utils.getEmployeeDetails
import com.asl_emp_mng.app.utils.getIsCOMPANYLogin
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import java.io.File
import java.time.LocalDate

class EmplyeeyerProfile : AppCompatActivity() {

    private lateinit var binding:ActivityEmplyeeyerProfileBinding
    private var profileImage: File? = null

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmplyeeyerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView(binding)
        setOnClickEvents(binding)
        observeViewModel()
    }

    private fun setupView(binding: ActivityEmplyeeyerProfileBinding) {
        binding?.apply {
            Log.e("TAG", "setupView: ${Gson().toJson(getEmployeeDetails())}")
            if (getIsCOMPANYLogin() == true) {
                tvHeaderEmpName.text = getCompanyDetails()?.companyName ?: "Guest"
                tvHeaderEmpEmail.text = getCompanyDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.VISIBLE
                binding.llEmployerProfile.visibility = View.GONE
                binding.ivChangePicture.visibility = View.GONE
            } else {

                settingsViewModel.fetchEmployeeDetails(this@EmplyeeyerProfile, getEmployeeDetails()?.id.toString())

                tvHeaderEmpName.text = getEmployeeDetails()?.name ?: "Guest"
                tvHeaderEmpEmail.text = getEmployeeDetails()?.email ?: "--"
                binding.llCompanyProfile.visibility = View.GONE
                binding.llEmployerProfile.visibility = View.VISIBLE
                binding.ivChangePicture.visibility = View.VISIBLE

            }
        }
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.fetchEmployeeDetails(this@EmplyeeyerProfile, getEmployeeDetails()?.id.toString())

    }


    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeUploadImageResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                settingsViewModel.fetchEmployeeDetails(this@EmplyeeyerProfile, getEmployeeDetails()?.id.toString())
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
                        .placeholder(R.drawable.demo_avatar)
                        .error(R.drawable.demo_avatar)
                        .into(binding.ivHeaderProfilePic)

                    Log.d("res","get iamge url $imageUrl")
                } else {
                    CustomToast(this, "No image available")
                }
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

        val departmentSettings = binding?.expandableAccountSetting?.findViewById<AppCompatTextView>(R.id.tv_department_settings)

        departmentSettings?.setOnClickListener {
            startActivity(Intent(this, AddDepartmentActivity::class.java))
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

        val setAttendanceSetting =
            binding?.expandableAttandancenManagement?.findViewById<AppCompatTextView>(R.id.tv_set_attendance_settings)




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

        // for employee

        binding?.tvLeave?.setOnClickListener {
            startActivity(Intent(this, EmpLeaveActivity::class.java))
        }
        binding?.tvAttendance?.setOnClickListener {
            startActivity(Intent(this,EmployeeAttendanceRecordActivity::class.java))
        }
        binding?.expandableProfile?.setOnClickListener {
            binding.expandableProfile.toggleLayout()

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
        val educationalProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_educational)
        educationalProfile?.setOnClickListener {

            CustomToast(this, "Working is progress")
            /*  val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
              intent.putExtra("PROFILE_TYPE", ProfileType.EDUCATION.name)
              startActivity(intent)*/
        }
        val documentProfile =
            binding?.expandableProfile?.findViewById<AppCompatTextView>(R.id.tv_profile_documents)
        documentProfile?.setOnClickListener {
            CustomToast(this, "Working is progress")
            /*  val intent = Intent(this@EmplyeeyerProfile, EmpProfileActivity::class.java)
              intent.putExtra("PROFILE_TYPE", ProfileType.DOCUMENT.name)
              startActivity(intent)*/
        }

        binding.tvEmpLogout.setOnClickListener {
            doLogout(this)
        }
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
                        Log.d("res", "File selected: ${file.absolutePath}")
                        val id = getEmployeeDetails()?.id

                        id?.let {
                            settingsViewModel.changeEmpProfileImage(this, it, file)
                        } ?: Log.e("res", "Employee ID is null")
                    }

                }
            } else {
                CustomToast(this, "File selection failed")

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CustomToast(this, ImagePicker.getError(data))

        } else {
            CustomToast(this, "Task Cancelled")

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