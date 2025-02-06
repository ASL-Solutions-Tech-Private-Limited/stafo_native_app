package com.asl_emp_mng.app.screens

import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.DynamicAdapter
import com.asl_emp_mng.app.base.model.DynamicField
import com.asl_emp_mng.app.databinding.ActivityCreateLeavePolicyBinding
import com.asl_emp_mng.app.databinding.ActivityLeaveManagementBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Collections
import java.util.Random

class CreateLeavePolicyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateLeavePolicyBinding
    private lateinit var adapter: DynamicAdapter
    private val dynamicFields = mutableListOf<DynamicField>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCreateLeavePolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
            }

            btnAddPolicy.setOnClickListener {
               if (adapter.isValid()){

               }
            }




        }
    }

    private fun addDynamicField() {
        val options = resources.getStringArray(R.array.leave_type).toList()
        dynamicFields.add(DynamicField("Enter Number of Leave", options))
        adapter = DynamicAdapter(dynamicFields)
        adapter.notifyItemInserted(dynamicFields.size - 1)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.setLayoutManager(layoutManager)
        binding.recyclerView.adapter = adapter
    }
}