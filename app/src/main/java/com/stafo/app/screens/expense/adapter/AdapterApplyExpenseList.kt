package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemApplyExpenseChildLayoutBinding
import com.stafo.app.screens.expense.EmployeeApplyExpenseActivity
import com.stafo.app.screens.expense.ViewDetailsApplyExpenseActivity
import com.stafo.app.screens.expense.dataClass.GetExpenseList

class AdapterApplyExpenseList(
    var context: Activity,
    private var list:List<GetExpenseList>,
    private val onApproveClick: (GetExpenseList) -> Unit,
    private val onRejectClick: (GetExpenseList) -> Unit
) : RecyclerView.Adapter<AdapterApplyExpenseList.ViewHolder>() {
    inner class ViewHolder(val binding: ItemApplyExpenseChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplyExpenseChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         with(holder) {
             with(list[position]) {

                 binding.tvCategoryTitle.text = expenseType
                 binding.tvDate.text = date
                 binding.tvAmount.text = amount.toDoubleOrNull()?.toInt()?.let { "₹ $it" } ?: "₹ 0"
                 binding.tvStatus.text = status

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



                 itemView.setOnClickListener {
                     context.startActivity(Intent(context, ViewDetailsApplyExpenseActivity::class.java))
                 }

                 binding.btnApprove.setOnClickListener {
                     onApproveClick(this)
                 }

                 binding.btnReject.setOnClickListener {
                     onRejectClick(this)
                 }

                 binding.ivView.setOnClickListener {
                     context.startActivity(Intent(context, EmployeeApplyExpenseActivity::class.java))

                 }
             }


         }
    }
    fun updateList(newList: List<GetExpenseList>) {
        this.list = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return list.size
    }
}