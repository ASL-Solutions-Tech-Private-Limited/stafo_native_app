package com.stafo.app.screens.tms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemAttachDocLsitBinding
import com.stafo.app.screens.tms.AttachmentAdapter
import com.stafo.app.screens.tms.dataClass.AssignTaskEmp

class AdapterAssignTaskEmp (private val items: List<AssignTaskEmp>) :
    RecyclerView.Adapter<AdapterAssignTaskEmp.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(val binding: ItemAttachDocLsitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachDocLsitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.binding.tvFileName.text = items[position].name
        // Add icon or remove button logic here if needed
    }

    override fun getItemCount(): Int = items.size
}