package com.stafo.app.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.R
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.databinding.ActivitySetAttendanceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import java.io.File

class SetAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetAttendanceBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: EmpListAdapter
    private var empList: List<GetEmployee> = listOf()
    private var filteredList: List<GetEmployee> = listOf()

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var attendanceType: String = "false"
    private var mCurrentSelfieEmpId = -1
    private val REQUEST_CODE_SELFIE_PICKER = 3001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySetAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnClickEvents()
        observeViewModel()
        setupSearchListener()
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getAllEmployeeList(this)
    }

    private fun setOnClickEvents() {
        binding.apply {
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                settingsViewModel.getAllEmployeeList(this@SetAttendanceActivity)
            }
        }
    }

    private fun observeViewModel() {
        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {
            if (it.status && it.data.isNotEmpty()) {
                empList = it.data
                filteredList = empList

                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvEmpList.layoutManager = layoutManager
                rvAdapter = EmpListAdapter(empList, this, "SetAttendance", object : EmpListAdapter.onGeoClick {
                    override fun onEMPClick(empID: String, type: String) {
                        if (type == "Request Location") {
                            settingsViewModel.sendGeoLocationRequest(
                                this@SetAttendanceActivity,
                                empID, "1"
                            )
                        }
                    }
                })
                binding.rvEmpList.adapter = rvAdapter
            }
        }

        settingsViewModel.mSetAttendanceTypeResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                if (::bottomSheetDialog.isInitialized && bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                }
                settingsViewModel.getAllEmployeeList(this)
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mSelfieUploadResponse.observe(this) {
            if (it.status) {
                CustomToast(this, "Selfie image uploaded successfully!")
                settingsViewModel.getAllEmployeeList(this)
                if (mCurrentSelfieEmpId != -1) {
                    val request = SetAttendanceTypeRequest(
                        employee_id = mCurrentSelfieEmpId,
                        attendance_type = "selfie"
                    )
                    settingsViewModel.setAttendanceTypeEmployee(this, request)
                }
            } else {
                CustomToast(this, it.message)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    fun showCustomBottomSheet(id: Int, getAttendanceType: String?) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.attendance_mode_bottom_sheet_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        val switchAllow = view.findViewById<SwitchCompat>(R.id.switch_allow)
        val switchSelfie = view.findViewById<SwitchCompat>(R.id.switch_selfie)
        val switchQr = view.findViewById<SwitchCompat>(R.id.switch_qr)
        val switchGeo = view.findViewById<SwitchCompat>(R.id.switch_geo)
        val btnSetAttendanceType = view.findViewById<AppCompatButton>(R.id.btn_setAttendance_type)

        when (getAttendanceType) {
            "geo" -> {
                switchGeo.isChecked = true
                attendanceType = "geo"
            }
            "qr code" -> {
                switchQr.isChecked = true
                attendanceType = "qr code"
            }
            "selfie" -> {
                switchSelfie.isChecked = true
                attendanceType = "selfie"
            }
        }

        switchAllow.setOnCheckedChangeListener { _, isChecked ->
            attendanceType = if (isChecked) "true" else "false"
        }

        switchGeo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceType = "geo"
                switchQr.isChecked = false
                switchSelfie.isChecked = false
            }
        }

        switchQr.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceType = "qr code"
                switchGeo.isChecked = false
                switchSelfie.isChecked = false
            }
        }

        val btnChangePhoto = view.findViewById<TextView>(R.id.btn_change_selfie_photo)

        val selectedEmp = empList.find { it.id == id }
        val hasSelfie = selectedEmp?.hasSelfie == true || !selectedEmp?.selfieImage.isNullOrBlank() || !selectedEmp?.selfieImagePath.isNullOrBlank()

        if (hasSelfie) {
            btnChangePhoto.visibility = View.VISIBLE
            btnChangePhoto.setOnClickListener {
                showChooseSelfieSourceDialog(id)
            }
        } else {
            btnChangePhoto.visibility = View.GONE
        }

        switchSelfie.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (!hasSelfie) {
                    buttonView.isChecked = false
                    showNoSelfieDialog(id)
                } else {
                    attendanceType = "selfie"
                    switchGeo.isChecked = false
                    switchQr.isChecked = false
                    btnChangePhoto.visibility = View.VISIBLE
                }
            } else {
                if (!hasSelfie) {
                    btnChangePhoto.visibility = View.GONE
                }
            }
        }

        btnSetAttendanceType.setOnClickListener {
            if (attendanceType == "false") {
                CustomToast(this, "Please select attendance type!")
            } else {
                val request = SetAttendanceTypeRequest(
                    employee_id = id,
                    attendance_type = attendanceType
                )
                settingsViewModel.setAttendanceTypeEmployee(this, request)
            }
        }

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showNoSelfieDialog(employeeId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_no_selfie_warning, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel_warning)
        val btnAddSelfie = dialogView.findViewById<View>(R.id.btn_add_selfie_warning)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnAddSelfie.setOnClickListener {
            dialog.dismiss()
            showChooseSelfieSourceDialog(employeeId)
        }

        dialog.show()
    }

    private fun showChooseSelfieSourceDialog(employeeId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_selfie_source, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val optionCamera = dialogView.findViewById<View>(R.id.ll_option_camera)
        val optionGallery = dialogView.findViewById<View>(R.id.ll_option_gallery)
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel_source)

        mCurrentSelfieEmpId = employeeId

        optionCamera.setOnClickListener {
            dialog.dismiss()
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(REQUEST_CODE_SELFIE_PICKER)
        }

        optionGallery.setOnClickListener {
            dialog.dismiss()
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(REQUEST_CODE_SELFIE_PICKER)
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELFIE_PICKER && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data!!
            val file = getFileFromUri(uri)
            if (file != null && mCurrentSelfieEmpId != -1) {
                settingsViewModel.uploadSelfieAttendance(this, mCurrentSelfieEmpId.toString(), file)
            } else {
                CustomToast(this, "File selection failed")
            }
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

    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }

        if (::rvAdapter.isInitialized) {
            rvAdapter.updateList(filteredList)
        }
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }
}
