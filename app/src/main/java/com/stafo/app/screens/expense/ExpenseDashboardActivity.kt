package com.stafo.app.screens.expense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityExpenseDashboardBinding
import com.stafo.app.screens.expense.adapter.AdapterApplyExpenseList
import com.stafo.app.screens.expense.adapter.AdapterExpenseCategory
import com.stafo.app.screens.expense.dataClass.ApplyExpenseData
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import com.stafo.app.screens.expense.dataClass.GetExpenseList
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class ExpenseDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseDashboardBinding
    private lateinit var rvAdapter: AdapterApplyExpenseList
    private lateinit var expenseList: List<ApplyExpenseData>

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

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
        observeViewModel()
    }


    private fun onClickListener() {
        binding.apply {

            if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)){
                getEmployeeComId()?.let {
                    expenseViewModel.getApplyExpenseList(
                        this@ExpenseDashboardActivity,
                       companyId =  it
                    )
                }
            }else{
                expenseViewModel.getApplyExpenseList(
                    this@ExpenseDashboardActivity,
                    employeeId = getEmployeeDetails()?.id.toString()
                )
            }





            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing=false
                if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)){
                    getEmployeeComId()?.let {
                        expenseViewModel.getApplyExpenseList(
                            this@ExpenseDashboardActivity,
                            companyId = it
                        )
                    }
                }else{
                    expenseViewModel.getApplyExpenseList(
                        this@ExpenseDashboardActivity,
                        employeeId = getEmployeeDetails()?.id.toString()
                    )
                }
            }

            btnAddExpense.setOnClickListener {

                if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)){
                    startActivity(Intent(this@ExpenseDashboardActivity,CreateExpenseActivity::class.java))
                }else{
                    startActivity(Intent(this@ExpenseDashboardActivity,EmployeeApplyExpenseActivity::class.java))
                }


            }


            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            val filterMap = listOf(
                binding.tvAllTasks to "All",
                binding.tvPending to "Pending",
                binding.tvCompleted to "Approved",
                binding.tvRejected to "Rejected"
            )

            filterMap.forEach { (textView, status) ->
                textView.setOnClickListener {
                    if (::rvAdapter.isInitialized) {
                        txtMsg.visibility=View.GONE
                        updateTabUI(textView)
                        filterByStatus(status)
                    } else{
                        updateTabUI(textView)
                        txtMsg.visibility=View.VISIBLE
                    }
                }
            }






        }
    }



    private fun observeViewModel() {
        expenseViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        expenseViewModel.mViewApplyExpenseResponse.observe(this) { it ->
            if (it.success) {
                if (!it.data.isNullOrEmpty()) {
                    binding.rvShowApplyExpense.visibility=View.VISIBLE
                    binding.txtMsg.visibility=View.GONE


                    expenseList=it.data

                    rvAdapter = AdapterApplyExpenseList(
                        context = this,
                        expenseList,
                        onApproveClick = { position ->
                            Log.d("exp", "setUpRecyclerView: $position")

                        },
                        onRejectClick = { position ->
                            Log.e("exp", "setUpRecyclerView: $position")
                        }
                    )
                    binding.rvShowApplyExpense.adapter = rvAdapter
                    binding.rvShowApplyExpense.layoutManager = LinearLayoutManager(this)
                    rvAdapter.notifyDataSetChanged()
                }else{
                    binding.rvShowApplyExpense.visibility=View.GONE
                    binding.txtMsg.visibility=View.VISIBLE
                }
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












   /* private fun setUpRecyclerView(){
         expenseList = listOf(
            GetExpenseList(
                id = 1,
                employee_id = 101,
                employee = "John Doe",
                expenseType = "Travel",
                date = "2025-06-01",
                amount = "1200.00",
                status = "Pending"
            ),
            GetExpenseList(
                id = 2,
                employee_id = 102,
                employee ="Jane Smith",
                expenseType = "Meal",
                date = "2025-06-03",
                amount = "450.00",
                status = "Approved"
            ),
            GetExpenseList(
                id = 3,
                employee_id = 103,
                employee = "Alice Brown",
                expenseType = "Lodging",
                date = "2025-06-04",
                amount = "2300.00",
                status = "Rejected"
            )
        )




        rvAdapter = AdapterApplyExpenseList(
            context = this,
            expenseList,
            onApproveClick = { position ->
                Log.d("exp", "setUpRecyclerView: $position")

            },
            onRejectClick = { position ->
                Log.e("exp", "setUpRecyclerView: $position")
            }
        )
        binding.rvShowApplyExpense.adapter = rvAdapter
        binding.rvShowApplyExpense.layoutManager = LinearLayoutManager(this)
    }*/
    private fun filterByStatus(status: String) {
        val filteredList = if (status == "All") {
            expenseList
        } else {
            expenseList.filter { it.status.equals(status, ignoreCase = true) }
        }

        rvAdapter.updateList(filteredList)
    }
    private fun updateTabUI(selected: AppCompatTextView) {
        val tabViews = listOf(
            binding.tvAllTasks,
            binding.tvPending,
            binding.tvCompleted,
            binding.tvRejected
        )

        tabViews.forEach { textView ->
            val isSelected = textView == selected
            textView.background = ContextCompat.getDrawable(
                this,
                if (isSelected) R.drawable.select_card_rtl_bg_offer else R.drawable.card_rtl_bg_offer
            )
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.white else R.color.black
                )
            )
        }
    }


    private fun companyAlertDialog(msg:String,expId:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }



}