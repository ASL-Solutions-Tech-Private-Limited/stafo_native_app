package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemExpenseCategoryBinding
import com.stafo.app.screens.expense.dataClass.ExpenseCategory

class AdapterExpenseCategory (
    private var list: List<ExpenseCategory>,
    var context: Activity,
) : RecyclerView.Adapter<AdapterExpenseCategory.ViewHolder>() {
    inner class ViewHolder(val binding: ItemExpenseCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseCategoryBinding.inflate(
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
                binding.tvCategoryTitle.text = this.categoryName
                binding.tvAttachRequired.text = if (this.attachDocumentRequired) "Yes" else "No"

                val adapter = AdapterExpenseCategoryFields(this.requiredFields,context)
                binding.rvRequiredFields.adapter = adapter
                binding.rvRequiredFields.layoutManager = LinearLayoutManager(context)
                adapter.notifyDataSetChanged()

            }


        }
    }
}