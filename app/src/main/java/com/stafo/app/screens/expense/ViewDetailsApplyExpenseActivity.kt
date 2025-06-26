package com.stafo.app.screens.expense

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityViewDetailsApplyExpenseBinding
import com.stafo.app.screens.expense.adapter.AdapterApplyExpenseList
import com.stafo.app.screens.expense.dataClass.ExpenseChangeStatusRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.formatCreatedAtDate
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin

class ViewDetailsApplyExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewDetailsApplyExpenseBinding

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var expenseId:Int=0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityViewDetailsApplyExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        expenseId = intent.getIntExtra("expense_id", -1)

        Log.e("expense","get : $expenseId")
        setOnClickListener()
        observeViewModel()




    }

    private fun setOnClickListener(){
        binding.apply {
           imageBack.setOnClickListener {
               onBackPressedDispatcher.onBackPressed()
               finish()
           }


            expenseViewModel.viewExpenseDetails(this@ViewDetailsApplyExpenseActivity,expenseId)

            btnApprove.setOnClickListener {
                companyAlertDialog("Approved",expenseId)
            }

            btnReject.setOnClickListener {
                companyAlertDialog("Rejected",expenseId)
            }
            btnDelete.setOnClickListener {
                employeeAlertDialog(expenseId)
            }

            btnEdit.setOnClickListener {
                val intent = Intent(this@ViewDetailsApplyExpenseActivity, EmployeeApplyExpenseActivity::class.java)
                intent.putExtra("expense_id", expenseId)
                startActivity(intent)
            }


        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        expenseViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        expenseViewModel.mExpenseChangeStatusResponse.observe(this) { it ->
            if (it.status) {
                CustomToast(this,it.message)
                expenseViewModel.viewExpenseDetails(this@ViewDetailsApplyExpenseActivity,expenseId)

            } else   CustomToast(this,it.message)
        }

        expenseViewModel.mViewExpenseDetailsResponse.observe(this) { it ->
            if (it.success) {

                binding.rtlExpense.visibility=View.VISIBLE
                binding.txtMsg.visibility=View.GONE

                binding.tvCategoryTitle.text=it.data.expense_type.name
                binding.tvDate.text=formatCreatedAtDate(it.data.created_at)
                binding.tvAmount.text = it.data.amount.toDoubleOrNull()?.toInt()?.let { "₹ $it" } ?: "₹ 0"
                binding.tvStatus.text = it.data.status

                binding.llExpenseFields.removeAllViews()


                it.data.expense_details.forEach { detail ->
                    val expenseForm = detail.expense_form
                    if (expenseForm != null) {
                        val fieldName = expenseForm.field_name
                        val fieldValue = detail.expense_value

                        val textView = TextView(this).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            text = "$fieldName :   $fieldValue"
                            setTextColor(ContextCompat.getColor(this@ViewDetailsApplyExpenseActivity, R.color.black))
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            typeface = ResourcesCompat.getFont(this@ViewDetailsApplyExpenseActivity, R.font.gilroy_medium)
                        }

                        binding.llExpenseFields.addView(textView)
                    }
                }




                if (getIsCOMPANYLogin(this)){
                    if (it.data.status=="Pending"){
                        binding.tvStatus.setTextColor(resources.getColor(R.color.pending_colour))
                        binding.llcCompanyAction.visibility=View.VISIBLE
                        binding.llcEmployeeAction.visibility=View.GONE

                    }
                    else if (it.data.status=="Approved") {
                        binding.tvStatus.setTextColor(resources.getColor(R.color.green))
                        binding.llcCompanyAction.visibility=View.GONE
                    }
                    else if (it.data.status=="Rejected") {
                        binding.tvStatus.setTextColor(resources.getColor(R.color.pastel_red))
                        binding.llcCompanyAction.visibility=View.GONE
                    }
                }else{
                    if (it.data.status=="Pending"){
                        binding.tvStatus.setTextColor(resources.getColor(R.color.pending_colour))
                        binding.llcCompanyAction.visibility=View.GONE
                        binding.llcEmployeeAction.visibility=View.VISIBLE

                    }
                    else if (it.data.status=="Approved") {
                        binding.tvStatus.setTextColor(resources.getColor(R.color.green))
                        binding.llcEmployeeAction.visibility=View.GONE
                    }
                    else if (it.data.status=="Rejected") {
                        binding.tvStatus.setTextColor(resources.getColor(R.color.pastel_red))
                        binding.llcEmployeeAction.visibility=View.GONE
                    }
                }



            } else {
                binding.rtlExpense.visibility=View.GONE
                binding.txtMsg.visibility=View.VISIBLE
                CustomToast(this,it.message)
            }
        }


        expenseViewModel.mEmployeeDeleteExpenseResponse.observe(this) { it ->
            if (it.status) {
                CustomToast(this,it.message)
             onBackPressedDispatcher.onBackPressed()
                finish()

            } else   CustomToast(this,it.message)
        }

    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun companyAlertDialog(status:String, expId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Are you sure? You want to change status this item?")
            .setPositiveButton("Yes") { dialog, _ ->

                val request= ExpenseChangeStatusRequest(
                    id = expId,
                    status=status
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