package com.asl_emp_mng.app.screens.settings

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.AdapterPolicy
import com.asl_emp_mng.app.base.adapter.DynamicHolidayAdapter
import com.asl_emp_mng.app.base.adapter.DynamicPolicyAdapter
import com.asl_emp_mng.app.base.model.DynamicHolidayField
import com.asl_emp_mng.app.base.model.DynamicPolicyField
import com.asl_emp_mng.app.databinding.ActivityAddPolicyBinding
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import org.osmdroid.tileprovider.cachemanager.CacheManager.getFileName
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPolicyBinding
    private lateinit var adapter: DynamicPolicyAdapter
    private var selectedPosition: Int = -1
    private val dynamicFields = mutableListOf<DynamicPolicyField>()


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setupRecyclerView()
        addDynamicField()
        onClickListener()
        observeViewModel()

    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mPolicyCreateResponse.observe(this) {

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

    private fun onClickListener() {
        binding?.apply {


          /*  llcAddMore.setOnClickListener {

                CustomToast(this@AddPolicyActivity,"Working is progress")
                //addDynamicField()
            }*/



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            /* btnAddPolicy.setOnClickListener {
                 if (adapter.isValid()) {





                 } else {
                     CustomToast(this@AddPolicyActivity, "Please fill blank field!")
                 }
             }*/

            btnAddPolicy.setOnClickListener {
                if (adapter.isValid()) {
                    val policies = adapter.fields

                    for (policy in policies) {
                        val title = policy.userInput
                        val fileUri = Uri.parse(policy.userInput2)

                        if (title.isNotEmpty() && fileUri != null) {
                            val file = uriToFile(fileUri)

                            if (file != null) {
                                settingsViewModel.uploadPolicy(
                                    this@AddPolicyActivity,
                                    title,
                                    "Policy Description",
                                    file
                                )
                            } else {
                                CustomToast(this@AddPolicyActivity, "File conversion failed!")
                            }
                        } else {
                            CustomToast(this@AddPolicyActivity, "Missing title or file!")
                        }
                    }
                } else {
                    CustomToast(this@AddPolicyActivity, "Please fill blank fields!")
                }
            }


        }
    }


    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleFile(it, selectedPosition)
            }
        }

    private fun handleFile(uri: Uri, position: Int) {
        val fileType = contentResolver.getType(uri)
        val filePath = uri.toString()

        println("File Selected - Position: $position, Path: $filePath")

        if (fileType == "application/pdf" ||
            fileType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) {
            adapter.fields[position].userInput2 = filePath
            adapter.notifyItemChanged(position)

        } else {
            CustomToast(this@AddPolicyActivity, "Invalid file type. Please select PDF or DOCX.")
        }
    }


    fun Context.uriToFile(uri: Uri): File? {
        val contentResolver: ContentResolver = this.contentResolver
        val file = File(cacheDir, getFileName(uri))

        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Context.getFileName(uri: Uri): String {
        var name = "temp_file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun setupRecyclerView() {
        adapter = DynamicPolicyAdapter(dynamicFields) { position ->
            selectedPosition = position
            pickFileLauncher.launch("*/*")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun addDynamicField() {
        val newField = DynamicPolicyField("Enter Policy Name", "Browse File")

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicPolicyAdapter(dynamicFields) { position ->
                selectedPosition = position
                pickFileLauncher.launch("*/*")
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }


}