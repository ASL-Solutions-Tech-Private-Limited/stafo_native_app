package com.stafo.app.screens.expense

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateExpenseBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter
import com.stafo.app.screens.expense.adapter.DynamicExpenseAdapter
import com.stafo.app.screens.expense.dataClass.DynamicExpenseField
import com.stafo.app.screens.expense.dataClass.ExpenseFormCreateRequest
import com.stafo.app.screens.expense.dataClass.ExpenseFormTypeList
import com.stafo.app.screens.expense.dataClass.Field
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class CreateExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateExpenseBinding


    private lateinit var adapter: DynamicExpenseAdapter
    private val dynamicFields = mutableListOf<DynamicExpenseField>()

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var actionMode:Boolean=false
    private var expenseFromIndex:Int=0

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
        observeViewModel()


    }

    private fun onClickListener() {
        binding.apply {

            val expenseFormData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("expense_form_data", ExpenseFormTypeList::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("expense_form_data") as? ExpenseFormTypeList
            }

            expenseFormData?.let { data ->
                actionMode=true
                expenseFromIndex=data.id
                ivViewExpense.visibility=View.GONE
                tvPageName.text="Edit Expense"
                btnCreateExpenseForm.text="Update Expense Form"

                binding.tieExpenseType.setText(data.name)
                binding.checkBox.isChecked = data.is_document_req.equals("Yes", ignoreCase = true)

                val loadedFields = data.expense_forms?.map {
                    DynamicExpenseField(
                        inputType = it.field_type,
                        userInput = it.field_name
                    )
                } ?: emptyList()

                dynamicFields.addAll(loadedFields)
                adapter.notifyDataSetChanged()
            }




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
                Log.e("expense", "onClickListener")

                val expenseType = tieExpenseType.text.toString().trim()
                val isDocumentRequired = if (checkBox.isChecked) "Yes" else "No"
                Log.e("expense","$isDocumentRequired")
                if (!tieExpenseType.text.toString().isNullOrEmpty()) {
                    if (dynamicFields.size > 0) {
                        if (adapter.isValid()) {
                            val allFields = adapter.getAllFields()

                            val fieldList = allFields.map {
                                Field(
                                    fieldName = it.userInput,
                                    fieldType = it.inputType,
                                    description = ""
                                )
                            }

                            val request = ExpenseFormCreateRequest(
                                company_id = getEmployeeComId().toString(),
                                type_name = expenseType,
                                description = "",
                                isDocumentReq = isDocumentRequired,
                                fields = fieldList
                            )
                            Log.e("expense",request.toString())
                            // Call API
                            if (actionMode){
                                expenseViewModel.updateExpenseForm(this@CreateExpenseActivity, expenseFromIndex,request)
                            }else{
                                expenseViewModel.expenseFormCreate(this@CreateExpenseActivity, request)
                            }




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


    private fun observeViewModel() {
        expenseViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        expenseViewModel.mExpenseFormCreateResponse.observe(this) { it ->
            if (it.status){
                CustomToast(this,it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else  CustomToast(this,it.message)

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