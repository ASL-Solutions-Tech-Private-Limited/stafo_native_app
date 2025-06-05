package com.stafo.app.screens.expense

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEmployeeApplyExpenseBinding
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmployeeApplyExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeApplyExpenseBinding

    val expenseCategories = listOf(
        ExpenseCategory(
            id = 1, categoryName = "Travel", requiredFields = listOf(
                "From Location", "To Location", "Travel Date", "Mode of Transport"
            ), attachDocumentRequired = true
        ), ExpenseCategory(
            id = 2,
            categoryName = "Food",
            requiredFields = listOf("Meal Type", "Number of People", "Date", "Restaurant Name"),
            attachDocumentRequired = true
        ), ExpenseCategory(
            id = 3,
            categoryName = "Office Supplies",
            requiredFields = listOf("Item Name", "Quantity", "Purchase Date"),
            attachDocumentRequired = false
        ), ExpenseCategory(
            id = 4,
            categoryName = "Internet",
            requiredFields = listOf("Provider Name", "Billing Period", "Amount"),
            attachDocumentRequired = true
        ), ExpenseCategory(
            id = 5,
            categoryName = "Reimbursement",
            requiredFields = listOf("Expense Description", "Amount", "Date", "Approval Status"),
            attachDocumentRequired = false
        )
    )


    private val inputFieldMap = mutableMapOf<String, EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeApplyExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            val expenseTypeList = ArrayList<SearchListItem>().apply {
                expenseCategories.forEach { it ->
                    add(
                        SearchListItem(
                            id = it.id ?: 0, title = it.categoryName ?: "No Name"
                        )
                    )
                }
            }

            binding.tieExpenseType.setOnClickListener {
                val dialog = SearchableDialog(
                    this@EmployeeApplyExpenseActivity, expenseTypeList, "Expense Type"
                )
                dialog.setOnItemSelected(object : OnSearchItemSelected {
                    override fun onClick(position: Int, searchListItem: SearchListItem) {
                        dialog.dismiss()
                        val selectedCategory = expenseCategories.find { it.id == searchListItem.id }
                        if (selectedCategory != null && !isFinishing && !isDestroyed) {
                            renderFields(selectedCategory)
                            binding.tieExpenseType.setText(searchListItem.title)
                            btnSubmit.visibility = View.VISIBLE

                        } else btnSubmit.visibility = View.GONE
                    }
                })


                if (!isFinishing && !isDestroyed) {
                    dialog.show()
                }
            }

            btnSubmit.setOnClickListener {
                val inputs = inputFieldMap.mapValues { it.value.text.toString() }
                Log.e("exp", "Collected: $inputs")
            }


        }

    }


    @SuppressLint("MissingInflatedId")
    private fun renderFields(category: ExpenseCategory) {
        binding.dynamicFieldContainer.removeAllViews()
        inputFieldMap.clear()

        for (fieldName in category.requiredFields) {
            val fieldView = layoutInflater.inflate(
                R.layout.item_expense_dynamic_input, binding.dynamicFieldContainer, false
            )
            val editText = fieldView.findViewById<TextInputEditText>(R.id.tie_expense_dynamic_field)
            val hintEdit = fieldView.findViewById<TextInputLayout>(R.id.til_expense_dynamic_field)

            if (editText == null) {
                Log.e("exp", "EditText not found in layout!")
                continue
            }

            hintEdit.hint = fieldName
            inputFieldMap[fieldName] = editText

            if (fieldName.contains("Date", ignoreCase = true)) {
                editText.inputType = InputType.TYPE_NULL
                editText.isFocusable = false
                editText.isFocusableInTouchMode = false
                editText.setOnClickListener {
                    showDatePickerDialog(editText)
                }
            } else if (fieldName.contains(
                    "Amount", ignoreCase = true
                ) || fieldName.contains(
                    "Quantity", ignoreCase = true
                ) || fieldName.contains("Number", ignoreCase = true)
            ) {
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }

            binding.dynamicFieldContainer.addView(fieldView)
        }

        binding.btnAttach.visibility =
            if (category.attachDocumentRequired) View.VISIBLE else View.GONE
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(calendar.time)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}