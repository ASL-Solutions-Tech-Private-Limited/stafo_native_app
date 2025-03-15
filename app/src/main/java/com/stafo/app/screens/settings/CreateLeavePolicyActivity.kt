package com.stafo.app.screens.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.DynamicAdapter
import com.stafo.app.base.model.DynamicField
import com.stafo.app.databinding.ActivityCreateLeavePolicyBinding
import com.stafo.app.utils.CustomToast

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

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