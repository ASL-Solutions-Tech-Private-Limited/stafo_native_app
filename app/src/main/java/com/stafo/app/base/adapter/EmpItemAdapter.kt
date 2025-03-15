package com.stafo.app.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.Employee
import com.stafo.app.databinding.RecyEmpChildItemLayoutBinding

class EmpItemAdapter(
    private val items: ArrayList<Employee>
) :
    RecyclerView.Adapter<EmpItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyEmpChildItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvPosition.text = item.position
        if (position % 2 == 0) {
            holder.llMain.background = ContextCompat.getDrawable(
                context,
                R.drawable.custom_date_bg
            )

            holder.ivEdit.background = ContextCompat.getDrawable(
                context,
                R.drawable.custom_emp_item_bg2
            )
        } else {
            holder.llMain.background = ContextCompat.getDrawable(
                context,
                R.drawable.custom_week_off_bg
            )

            holder.ivEdit.background = ContextCompat.getDrawable(
                context,
                R.drawable.custom_emp_item_bg
            )
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
    class ViewHolder(binding: RecyEmpChildItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val llMain = binding.rlEmp
        val tvName = binding.tvEmpName
        val tvPosition = binding.tvEmpPosition
        val ivEdit = binding.ivEdit
    }
}