package com.stafo.app.screens.expense

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityExpenseCategoryBinding
import com.stafo.app.screens.expense.adapter.AdapterExpenseCategory
import com.stafo.app.screens.expense.dataClass.ExpenseCategory

class ExpenseCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityExpenseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setUpRecyclerView()



    }

    private fun setUpRecyclerView(){

        val expenseCategories = listOf(
            ExpenseCategory(
                categoryName = "Travel",
                requiredFields = listOf("From Location", "To Location", "Travel Date", "Mode of Transport"),
                attachDocumentRequired = true
            ),
            ExpenseCategory(
                categoryName = "Food",
                requiredFields = listOf("Meal Type", "Number of People", "Date", "Restaurant Name"),
                attachDocumentRequired = true
            ),
            ExpenseCategory(
                categoryName = "Office Supplies",
                requiredFields = listOf("Item Name", "Quantity", "Purchase Date"),
                attachDocumentRequired = false
            ),
            ExpenseCategory(
                categoryName = "Internet",
                requiredFields = listOf("Provider Name", "Billing Period", "Amount"),
                attachDocumentRequired = true
            ),
            ExpenseCategory(
                categoryName = "Reimbursement",
                requiredFields = listOf("Expense Description", "Amount", "Date", "Approval Status"),
                attachDocumentRequired = false
            )
        )

        val adapter = AdapterExpenseCategory(expenseCategories, this)
        binding.rvShowExpenseCategory.adapter = adapter
        binding.rvShowExpenseCategory.layoutManager = LinearLayoutManager(this)
    }
}