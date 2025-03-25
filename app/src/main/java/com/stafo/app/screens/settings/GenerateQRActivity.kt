package com.stafo.app.screens.settings

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityGenerateQractivityBinding
import com.stafo.app.databinding.GenerateQrCodeBottomSheetLayoutBinding
import com.stafo.app.screens.settings.dataClass.DataBranch
import com.stafo.app.screens.settings.dataClass.DataDepartment
import com.stafo.app.screens.settings.dataClass.GenerateQCodeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class GenerateQRActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGenerateQractivityBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var branchDialog: SearchableDialog
    private lateinit var departmentDialog: SearchableDialog

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()

    private var selectBranch: Int = 0
    private var selectDepartment: Int = 0

    //bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogBinding: GenerateQrCodeBottomSheetLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityGenerateQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        bottomSheetDialog = BottomSheetDialog(this)

        onClickListener()
        observeViewModel()



    }

    private fun onClickListener() {
        binding.apply {
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            getEmployeeComId()?.let { settingsViewModel.getDepartmentList(this@GenerateQRActivity, it) }
            getEmployeeComId()?.let { settingsViewModel.getBranchList(this@GenerateQRActivity, it) }

            tieBranch.setOnClickListener { branchDialog.show() }
            tieDepartment.setOnClickListener { departmentDialog.show() }



            btnSubmit.setOnClickListener {
                if (selectBranch != 0 && selectDepartment != 0) {
                    getEmployeeComId()?.let {

                        val request= GenerateQCodeRequest(
                            company_id=it.toInt(),
                            branch_id=selectBranch,
                            department_id=selectDepartment
                        )
                        settingsViewModel.generateQRCode(this@GenerateQRActivity, request)
                    }
                } else {
                    CustomToast(this@GenerateQRActivity, "Please select both Branch and Department")
                }
            }



        }


    }

    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mDepartmentListResponse.observe(this) {
            mDepartmentList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mDepartmentList,
                    "Department",
                    it1.tieDepartment
                )
            }
        }

        settingsViewModel.mBranchListResponse.observe(this) {
            mBranchList = it.data
            binding?.let { it1 ->
                setupSearchableDialog(
                    mBranchList,
                    "Branch",
                    it1.tieBranch
                )
            }
        }

        settingsViewModel.mGenerateQRCodeResponse.observe(this) { bitmap ->
            bitmap?.let {
                if (!(bottomSheetDialog.isShowing == true)) {
                    showCustomBottomSheet()
                    bottomSheetDialogBinding.ivQr.setImageBitmap(it)
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



    private fun showCustomBottomSheet() {
        bottomSheetDialogBinding = GenerateQrCodeBottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialogBinding.bottomSheetCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialogBinding.btnDownload.setOnClickListener {

            settingsViewModel.mGenerateQRCodeResponse.observe(this@GenerateQRActivity) { bitmap ->
                bitmap?.let {
                    val uri = saveBitmapToStorage(this@GenerateQRActivity, bitmap)
                    if (uri != null) {
                        CustomToast(this@GenerateQRActivity, "QR Code saved successfully!")
                    } else {
                        CustomToast(this@GenerateQRActivity, "Failed to save QR Code.")
                    }
                }
            }

        }

        bottomSheetDialogBinding.btnShare.setOnClickListener {

            settingsViewModel.mGenerateQRCodeResponse.observe(this@GenerateQRActivity) { bitmap ->
                bitmap?.let {
                    val uri = saveBitmapToStorage(this@GenerateQRActivity, bitmap)
                    if (uri != null) {
                        shareImage(this@GenerateQRActivity, uri)
                    } else {
                        CustomToast(this@GenerateQRActivity, "Failed to share QR Code.")
                    }
                }
            }

        }

        bottomSheetDialog.setContentView(bottomSheetDialogBinding.root)
        bottomSheetDialog.show()
    }



    /*private fun saveBitmapToStorage(context: Context, bitmap: Bitmap): Uri? {
        val filename = "QRCode_${System.currentTimeMillis()}.png"
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return uri
    }*/

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap): Uri? {
        val filename = "QR_Code_${System.currentTimeMillis()}.jpg"


        val newWidth = bitmap.width * 2
        val newHeight = bitmap.height * 2
        val newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)

        val centerX = (newWidth - bitmap.width) / 2f
        val centerY = (newHeight - bitmap.height) / 2f
        canvas.drawBitmap(bitmap, centerX, centerY, null)





        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }


                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)

                return it
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }








    private fun shareImage(context: Context, uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }


}