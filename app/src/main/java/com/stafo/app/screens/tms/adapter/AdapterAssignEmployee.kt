package com.stafo.app.screens.tms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemTaskAssignEmployeeLayoutBinding
import com.stafo.app.screens.tms.dataClass.AssignedEmployee

class AdapterAssignEmployee (private val items: List<AssignedEmployee>) :
    RecyclerView.Adapter<AdapterAssignEmployee.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(val binding: ItemTaskAssignEmployeeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemTaskAssignEmployeeLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.binding.taskAssignedTo.text = items[position].name
        holder.binding.taskAssignedToRole.text = items[position].phone

    }

    override fun getItemCount(): Int = items.size
}