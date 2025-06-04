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
import com.stafo.app.databinding.ActivityExpenseDashboardBinding
import com.stafo.app.screens.expense.adapter.AdapterApplyExpenseList
import com.stafo.app.screens.expense.adapter.AdapterExpenseCategory
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import com.stafo.app.utils.CustomToast

class ExpenseDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseDashboardBinding
    private lateinit var rvAdapter: AdapterApplyExpenseList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityExpenseDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        setUpRecyclerView()
    }


    private fun onClickListener() {
        binding.apply {

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing=false
            }

            btnAddExpense.setOnClickListener {
                startActivity(Intent(this@ExpenseDashboardActivity,CreateExpenseActivity::class.java))
            }


            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }




        }
    }

    private fun setUpRecyclerView(){

        rvAdapter= AdapterApplyExpenseList(this)
        binding.rvShowApplyExpense.adapter = rvAdapter
        binding.rvShowApplyExpense.layoutManager = LinearLayoutManager(this)
    }



}