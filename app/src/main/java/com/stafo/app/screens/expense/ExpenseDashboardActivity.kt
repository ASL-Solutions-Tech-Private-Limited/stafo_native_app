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
import com.stafo.app.screens.expense.dataClass.ExpenseChangeStatusRequest
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

    private var userType: String = ""

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExpenseDashboardBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()
        if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)) {
            userType = "company"
            getEmployeeComId()?.let {
                expenseViewModel.getApplyExpenseList(
                    this@ExpenseDashboardActivity,
                    companyId = it
                )
            }
        } else {
            userType = "employee"
            expenseViewModel.getApplyExpenseList(
                this@ExpenseDashboardActivity,
                companyId = getEmployeeComId().toString(),
                employeeId = getEmployeeDetails()?.id.toString()
            )
        }
    }


    private fun onClickListener() {
        binding.apply {

            if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)) {
                userType = "company"
                getEmployeeComId()?.let {
                    expenseViewModel.getApplyExpenseList(
                        this@ExpenseDashboardActivity,
                        companyId = it
                    )
                }
            } else {
                userType = "employee"
                expenseViewModel.getApplyExpenseList(
                    this@ExpenseDashboardActivity,
                    companyId = getEmployeeComId().toString(),
                    employeeId = getEmployeeDetails()?.id.toString()
                )
            }





            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)) {
                    userType = "company"
                    getEmployeeComId()?.let {
                        expenseViewModel.getApplyExpenseList(
                            this@ExpenseDashboardActivity,
                            companyId = it
                        )
                    }
                } else {
                    userType = "employee"
                    expenseViewModel.getApplyExpenseList(
                        this@ExpenseDashboardActivity,
                        companyId = getEmployeeComId().toString(),
                        employeeId = getEmployeeDetails()?.id.toString()
                    )
                }
            }

            btnAddExpense.setOnClickListener {

                if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)) {
                    startActivity(
                        Intent(
                            this@ExpenseDashboardActivity,
                            CreateExpenseActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            this@ExpenseDashboardActivity,
                            EmployeeApplyExpenseActivity::class.java
                        )
                    )
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
                        txtMsg.visibility = View.GONE
                        updateTabUI(textView)
                        filterByStatus(status)
                    } else {
                        updateTabUI(textView)
                        txtMsg.visibility = View.VISIBLE
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
                    binding.rvShowApplyExpense.visibility = View.VISIBLE
                    binding.txtMsg.visibility = View.GONE


                    expenseList = it.data

                    rvAdapter = AdapterApplyExpenseList(
                        context = this,
                        expenseList,
                        userType,
                        onApproveClick = { expense ->
                            companyAlertDialog("Approved", expense.id)

                        },
                        onRejectClick = { expense ->
                            companyAlertDialog("Rejected", expense.id)
                        },
                        onEditClick = { expense ->
                            val intent = Intent(this@ExpenseDashboardActivity, EmployeeApplyExpenseActivity::class.java)
                            intent.putExtra("expense_id", expense.id)
                            startActivity(intent)
                        },
                        onDeleteClick = { expense ->
                            employeeAlertDialog(expense.id)
                        }

                    )
                    binding.rvShowApplyExpense.adapter = rvAdapter
                    binding.rvShowApplyExpense.layoutManager = LinearLayoutManager(this)
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.rvShowApplyExpense.visibility = View.GONE
                    binding.txtMsg.visibility = View.VISIBLE
                }
            }
        }

        expenseViewModel.mExpenseChangeStatusResponse.observe(this) { it ->
            if (it.status) {
                CustomToast(this, it.message)

                if (getIsCOMPANYLogin(this@ExpenseDashboardActivity)) {
                    userType = "company"
                    getEmployeeComId()?.let {
                        expenseViewModel.getApplyExpenseList(
                            this@ExpenseDashboardActivity,
                            companyId = it
                        )
                    }
                } else {
                    userType = "employee"
                    expenseViewModel.getApplyExpenseList(
                        this@ExpenseDashboardActivity,
                        companyId = getEmployeeComId().toString(),
                        employeeId = getEmployeeDetails()?.id.toString()
                    )
                }
            } else CustomToast(this, it.message)
        }

        expenseViewModel.mEmployeeDeleteExpenseResponse.observe(this) { it ->
            if (it.status) {
                CustomToast(this, it.message)
                userType = "employee"

                expenseViewModel.getApplyExpenseList(
                    this@ExpenseDashboardActivity,
                    companyId = getEmployeeComId().toString(),
                    employeeId = getEmployeeDetails()?.id.toString()
                )

            } else CustomToast(this, it.message)
        }

    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

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


    private fun companyAlertDialog(status: String, expId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Are you sure? You want to change status this item?")
            .setPositiveButton("Yes") { dialog, _ ->

                val request = ExpenseChangeStatusRequest(
                    id = expId,
                    status = status
                )

                expenseViewModel.approveRejectExpense(this, request)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun employeeAlertDialog(expId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Are you sure? You want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                expenseViewModel.deleteApplyExpense(this, expId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}