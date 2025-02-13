package com.asl_emp_mng.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.DynamicAdapter
import com.asl_emp_mng.app.base.model.DynamicField
import com.asl_emp_mng.app.databinding.ActivityCreateLeavePolicyBinding
import com.asl_emp_mng.app.utils.CustomToast

class CreateLeavePolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateLeavePolicyBinding
    private lateinit var adapter: DynamicAdapter
    private val dynamicFields = mutableListOf<DynamicField>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateLeavePolicyBinding.inflate(layoutInflater)
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
        adapter = DynamicAdapter(dynamicFields)
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
            }

            btnAddPolicy.setOnClickListener {
                if (adapter.isValid()) {
                    CustomToast(this@CreateLeavePolicyActivity, "ok!")
                } else {
                    CustomToast(this@CreateLeavePolicyActivity, "Please fill blank field!")
                }
            }


        }
    }

    private fun addDynamicField() {
        val options = resources.getStringArray(R.array.leave_type).toList()
        val newField = DynamicField("Enter Number of Leave", options)

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicAdapter(dynamicFields)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }

}