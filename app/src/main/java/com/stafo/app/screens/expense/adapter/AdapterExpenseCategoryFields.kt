package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyPlanViewChildLayoutBinding
import com.stafo.app.screens.expense.dataClass.ExpenseFormList


class AdapterExpenseCategoryFields(
    private var list: List<ExpenseFormList>,
    var context: Activity,
) : RecyclerView.Adapter<AdapterExpenseCategoryFields.ViewHolder>() {
    inner class ViewHolder(val binding: RecyPlanViewChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyPlanViewChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvItem.text = this.field_name
            }


        }
    }
}