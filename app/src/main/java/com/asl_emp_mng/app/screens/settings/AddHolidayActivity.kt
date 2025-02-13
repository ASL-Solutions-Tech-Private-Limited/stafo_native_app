package com.asl_emp_mng.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.DynamicAdapter
import com.asl_emp_mng.app.base.adapter.DynamicHolidayAdapter
import com.asl_emp_mng.app.base.model.DynamicField
import com.asl_emp_mng.app.base.model.DynamicHolidayField
import com.asl_emp_mng.app.databinding.ActivityAddHolidayBinding
import com.asl_emp_mng.app.utils.CustomToast

class AddHolidayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHolidayBinding
    private lateinit var adapter: DynamicHolidayAdapter
    private val dynamicFields = mutableListOf<DynamicHolidayField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddHolidayBinding.inflate(layoutInflater)
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


    private fun setupRecyclerView() {
        adapter = DynamicHolidayAdapter(dynamicFields)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
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

            btnAddHoliday.setOnClickListener {
                if (adapter.isValid()) {
                    val allFields = adapter.getAllFields()
                    for (field in allFields) {
                        println("Title: ${field.userInput}, Start Date: ${field.userInput2}, End Date: ${field.userInput3}")
                    }
                } else {
                    CustomToast(this@AddHolidayActivity, "Please fill blank field!")
                }
            }


        }
    }

    private fun addDynamicField() {
        val newField =
            DynamicHolidayField("Enter Holiday Name", "Enter Start Date", "Enter End Date")

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicHolidayAdapter(dynamicFields)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }
}