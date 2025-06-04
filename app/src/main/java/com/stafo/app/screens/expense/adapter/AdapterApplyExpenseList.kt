package com.stafo.app.screens.expense.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemApplyExpenseChildLayoutBinding
import com.stafo.app.screens.expense.ViewDetailsApplyExpenseActivity

class AdapterApplyExpenseList(
    var context: Activity,
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

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ViewDetailsApplyExpenseActivity::class.java))
        }
        /* with(holder) {
             with(list[position]) {


             }


         }*/
    }

    override fun getItemCount(): Int {
        return 5
    }
}