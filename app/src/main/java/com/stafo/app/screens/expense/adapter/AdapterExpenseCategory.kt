package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemExpenseCategoryBinding
import com.stafo.app.screens.expense.CreateExpenseActivity
import com.stafo.app.screens.expense.ExpenseCategoryActivity
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import com.stafo.app.screens.expense.dataClass.ExpenseFormTypeList

class AdapterExpenseCategory (
    private var list: List<ExpenseFormTypeList>,
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
                binding.tvCategoryTitle.text = this.name
                binding.tvAttachRequired.text = this.is_document_req?:""

                if (!this.expense_forms.isNullOrEmpty()){
                    val adapter = AdapterExpenseCategoryFields(this.expense_forms,context)
                    binding.rvRequiredFields.adapter = adapter
                    binding.rvRequiredFields.layoutManager = LinearLayoutManager(context)
                    adapter.notifyDataSetChanged()
                }

                binding.btnEdit.setOnClickListener {
                    val intent = Intent(context, CreateExpenseActivity::class.java)
                    intent.putExtra("expense_form_data", this)
                    context.startActivity(intent)
                }

                binding.btnDelete.setOnClickListener {
                    (context as ExpenseCategoryActivity).deleteExpense(this.id)
                }



            }


        }
    }
}