package com.asl_emp_mng.app.screens.settings

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.DynamicHolidayAdapter
import com.asl_emp_mng.app.base.adapter.DynamicPolicyAdapter
import com.asl_emp_mng.app.base.model.DynamicHolidayField
import com.asl_emp_mng.app.base.model.DynamicPolicyField
import com.asl_emp_mng.app.databinding.ActivityAddPolicyBinding
import com.asl_emp_mng.app.utils.CustomToast

class AddPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPolicyBinding
    private lateinit var adapter: DynamicPolicyAdapter
    private var selectedPosition: Int = -1
    private val dynamicFields = mutableListOf<DynamicPolicyField>()
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

        setupRecyclerView()
        addDynamicField()
        onClickListener()

    }

    private fun onClickListener() {
        binding?.apply {


            llcAddMore.setOnClickListener {
                addDynamicField()
            }



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            btnAddPolicy.setOnClickListener {
                if (adapter.isValid()) {
                    CustomToast(this@AddPolicyActivity, "ok!")
                } else {
                    CustomToast(this@AddPolicyActivity, "Please fill blank field!")
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