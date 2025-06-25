package com.stafo.app.screens.expense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityExpenseCategoryBinding
import com.stafo.app.screens.expense.adapter.AdapterExpenseCategory
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class ExpenseCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseCategoryBinding
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpenseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        //  setUpRecyclerView()
        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {
        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let {
                    expenseViewModel.getAllExpenseFormList(
                        this@ExpenseCategoryActivity,
                        it
                    )
                }

            }
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }



            getEmployeeComId()?.let {
                expenseViewModel.getAllExpenseFormList(
                    this@ExpenseCategoryActivity,
                    it
                )
            }


        }
    }

    private fun observeViewModel() {
        expenseViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        expenseViewModel.mGetAllExpenseFormListResponse.observe(this) { it ->
            if (it.success) {
                if (!it.data.isNullOrEmpty()) {
                    binding.txtMsg.visibility= View.GONE
                    binding.rvShowExpenseCategory.visibility= View.VISIBLE
                    val adapter = AdapterExpenseCategory(it.data, this)
                    binding.rvShowExpenseCategory.adapter = adapter
                    binding.rvShowExpenseCategory.layoutManager = LinearLayoutManager(this)
                    adapter.notifyDataSetChanged()
                }else{
                    binding.txtMsg.visibility= View.VISIBLE
                    binding.rvShowExpenseCategory.visibility= View.GONE
                }
            }
        }
        expenseViewModel.mDeleteExpenseFormResponse.observe(this) { it ->
            if (it.status) {
                CustomToast(this, it.message)
                getEmployeeComId()?.let {
                    expenseViewModel.getAllExpenseFormList(
                        this@ExpenseCategoryActivity,
                        it
                    )
                }
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


    fun deleteExpense(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->

                expenseViewModel.deleteExpenseForm(this, id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}