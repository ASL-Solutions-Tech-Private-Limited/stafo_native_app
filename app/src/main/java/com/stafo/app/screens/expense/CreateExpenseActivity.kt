package com.stafo.app.screens.expense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateExpenseBinding
import com.stafo.app.screens.expense.adapter.DynamicExpenseAdapter
import com.stafo.app.screens.expense.dataClass.DynamicExpenseField
import com.stafo.app.utils.CustomToast

class CreateExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateExpenseBinding


    private lateinit var adapter: DynamicExpenseAdapter
    private val dynamicFields = mutableListOf<DynamicExpenseField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)



        setupRecyclerView()
        onClickListener()


    }

    private fun onClickListener() {
        binding.apply {


            ivViewExpense.setOnClickListener {
                startActivity(
                    Intent(
                        this@CreateExpenseActivity, ExpenseCategoryActivity::class.java
                    )
                )

            }


            btnAddFields.setOnClickListener {
                addDynamicField()
            }



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            btnCreateExpenseForm.setOnClickListener {
                Log.e("exp", "onClickListener")


                if (!tieExpenseType.text.toString().isNullOrEmpty()) {
                    if (dynamicFields.size > 0) {
                        if (adapter.isValid()) {
                            val allFields = adapter.getAllFields()

                            val valuesOnly = allFields.map { it.userInput }
                            Log.e("exp", "All Values: $valuesOnly")

                        } else {
                            CustomToast(this@CreateExpenseActivity, "Please fill blank field!")
                        }


                    } else {
                        CustomToast(this@CreateExpenseActivity, "Please add expense filed!")
                    }
                } else CustomToast(this@CreateExpenseActivity, "Enter expense type")


            }


        }
    }

    private fun setupRecyclerView() {
        adapter = DynamicExpenseAdapter(dynamicFields)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun addDynamicField() {
        val newField = DynamicExpenseField()

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicExpenseAdapter(dynamicFields)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }
}