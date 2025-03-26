package com.stafo.app.screens.settings

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.stafo.app.R
import com.stafo.app.databinding.ActivityUploadSelfieAttendanceBinding
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class UploadSelfieAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadSelfieAttendanceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var mEmpID = ""

    private var selfieImage: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadSelfieAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mEmpID = intent.getStringExtra("EMP_ID").toString()

        onClickListener()
        observeViewModel()

    }

    private fun onClickListener() {
        binding.apply {

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            tvRetakeSelfie.setOnClickListener {
                openPicker(1101)
            }


            tvUploadSelfie.setOnClickListener {
                openPicker(1101)
            }


            btnUploadSelfie.setOnClickListener {
                if (selfieImage == null) {
                    CustomToast(this@UploadSelfieAttendanceActivity, "Please upload a selfie first")
                } else {
                    settingsViewModel.uploadSelfieAttendance(
                        this@UploadSelfieAttendanceActivity,
                        mEmpID,
                        selfieImage
                    )
                }
            }


        }


    }


    private fun bitmapToFile(bitmap: Bitmap, context: Context): File? {
        return try {

            val fileName = "selfie_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)
            file.createNewFile()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSelfieUploadResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
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

                        binding.sivEmpPunch.visibility = View.VISIBLE
                        binding.tvRetakeSelfie.visibility = View.VISIBLE

                        if (file != null) {
                            removeBackgroundUsingRemoveBg(file)
                        } else {

                            CustomToast(this,"Failed to get image file")
                        }

                        /* Glide.with(this)
                             .load(file)
                             .into(binding.sivEmpPunch)*/
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


    private fun removeBackgroundUsingRemoveBg(file: File) {
        val apiKey = "T13hChaJUYghAopjE1KoDe49"
        val url = "https://api.remove.bg/v1.0/removebg"


        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing image, please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image_file", file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .addFormDataPart("size", "auto")
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("X-Api-Key", apiKey)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    CustomToast(this@UploadSelfieAttendanceActivity, "Failed to upload image")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val inputStream = response.body?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    selfieImage=bitmapToFile(bitmap, this@UploadSelfieAttendanceActivity)

                    runOnUiThread {
                        binding.sivEmpPunch.setImageBitmap(bitmap)
                    }
                } else {
                    runOnUiThread {

                        CustomToast(
                            this@UploadSelfieAttendanceActivity,
                            "API Error: ${response.message}"
                        )
                    }
                }
            }
        })
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