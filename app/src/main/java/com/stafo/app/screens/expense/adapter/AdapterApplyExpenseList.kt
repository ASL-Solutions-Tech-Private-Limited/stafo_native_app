package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemApplyExpenseChildLayoutBinding
import com.stafo.app.screens.expense.EmployeeApplyExpenseActivity
import com.stafo.app.screens.expense.ViewDetailsApplyExpenseActivity
import com.stafo.app.screens.expense.dataClass.ApplyExpenseData
import com.stafo.app.screens.expense.dataClass.GetExpenseList
import com.stafo.app.utils.formatCreatedAtDate

class AdapterApplyExpenseList(
    var context: Activity,
    private var list:List<ApplyExpenseData>,
    private val userType: String,
    private val onApproveClick: (ApplyExpenseData) -> Unit,
    private val onRejectClick: (ApplyExpenseData) -> Unit,
    private val onEditClick: (ApplyExpenseData) -> Unit,
    private val onDeleteClick: (ApplyExpenseData) -> Unit
) : RecyclerView.Adapter<AdapterApplyExpenseList.ViewHolder>() {
    inner class ViewHolder(val binding: ItemApplyExpenseChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplyExpenseChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         with(holder) {
             with(list[position]) {

                binding.tvCategoryTitle.text = this.expense_type.name
                binding.tvDate.text = formatCreatedAtDate(this.created_at)
                 binding.tvAmount.text = this.amount.toDoubleOrNull()?.toInt()?.let { "₹ $it" } ?: "₹ 0"
                 binding.tvStatus.text = this.status

                 if (this.status=="Pending"){
                     binding.tvStatus.setTextColor(context.resources.getColor(R.color.pending_colour))
                     binding.contentLayout.visibility=android.view.View.VISIBLE

                 }
                 else if (this.status=="Approved") {
                     binding.tvStatus.setTextColor(context.resources.getColor(R.color.green))
                     binding.contentLayout.visibility=android.view.View.GONE
                 }
                 else if (this.status=="Rejected") {
                     binding.tvStatus.setTextColor(context.resources.getColor(R.color.pastel_red))
                     binding.contentLayout.visibility=android.view.View.GONE
                 }

                 val expenseId = this.id

                 Log.e("expense","send : $expenseId")

                 itemView.setOnClickListener {
                     val intent = Intent(context, ViewDetailsApplyExpenseActivity::class.java)
                     intent.putExtra("expense_id", expenseId)
                     context.startActivity(intent)
                 }

                 binding.ivView.setOnClickListener {
                     val intent = Intent(context, ViewDetailsApplyExpenseActivity::class.java)
                     intent.putExtra("expense_id", expenseId)
                     context.startActivity(intent)
                 }

                 if (userType == "company") {
                     binding.llcCompanyAction.visibility = View.VISIBLE
                     binding.llcEmployeeAction.visibility = View.GONE


                     binding.btnApprove.setOnClickListener { onApproveClick(this) }
                     binding.btnReject.setOnClickListener { onRejectClick(this) }

                 } else if (userType == "employee") {
                     binding.llcCompanyAction.visibility = View.GONE
                     binding.llcEmployeeAction.visibility = View.VISIBLE

                     binding.btnEdit.setOnClickListener { onEditClick(this) }
                     binding.btnDelete.setOnClickListener { onDeleteClick(this) }
                 }



             }


         }
    }
    fun updateList(newList: List<ApplyExpenseData>) {
        this.list = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return list.size
    }
}