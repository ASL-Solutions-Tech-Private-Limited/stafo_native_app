package com.stafo.app.screens.tms

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemAttachDocLsitBinding
import com.stafo.app.screens.tms.dataClass.TaskAttachment

class AttachmentAdapter(
    private val items: List<TaskAttachment>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(val binding: ItemAttachDocLsitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachDocLsitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.binding.tvFileName.text = "File ${position + 1}"
        holder.binding.root.setOnClickListener {
            onRemoveClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getAttachFiles(): List<TaskAttachment> = items
}




