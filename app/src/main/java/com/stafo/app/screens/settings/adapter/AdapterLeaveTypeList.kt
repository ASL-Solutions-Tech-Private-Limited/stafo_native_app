package com.stafo.app.screens.settings.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.CustomLeaveTypeItemLayoutBinding
import com.stafo.app.databinding.RecyBranchItemLayoutBinding
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.dataClass.LeaveItem

data class AdapterLeaveTypeList(
    private var list: List<LeaveItem>,
    var context: Activity,
    private val onEditClick: (LeaveItem) -> Unit,
    private val onDeleteClick: (LeaveItem) -> Unit
) : RecyclerView.Adapter<AdapterLeaveTypeList.ViewHolder>() {
    inner class ViewHolder(val binding: CustomLeaveTypeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomLeaveTypeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvCategoryTitle.text = this.name ?: ""
                binding.tvDate.text = this.no_of_days?.toString() ?: ""
                binding.tvStatus.text = this.description ?: ""
                binding.btnEdit.setOnClickListener { onEditClick(this) }
                binding.btnDelete.setOnClickListener { onDeleteClick(this) }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateList(newList: List<LeaveItem>) {
        list = newList
        notifyDataSetChanged()
    }

}