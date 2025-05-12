package com.stafo.app.screens.crm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemLeadsListBinding

// Lead Adapter to display the list of leads
class LeadAdapter(
    private var listener: (String) -> Unit
) : RecyclerView.Adapter<LeadAdapter.LeadViewHolder>() {

    // ViewHolder class to bind the item views
    inner class LeadViewHolder(val binding: ItemLeadsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            itemView.setOnClickListener {
                listener.invoke("clicked")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadViewHolder {
        val binding =
            ItemLeadsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeadViewHolder, position: Int) {
        // val lead = leadList[position]
        holder.bind()
    }

    override fun getItemCount(): Int = 5

    // Update the list dynamically
    /*fun updateList(newList: List<Lead>) {
        leadList = newList
        notifyDataSetChanged()
    }

    // Optionally: Handle the filtering functionality
    fun filter(query: String) {
        val filteredList = leadList.filter {
            it.name.contains(query, true) ||
                    it.company.contains(query, true) ||
                    it.phone.contains(query, true) ||
                    it.status.contains(query, true)
        }
        leadList = filteredList
        notifyDataSetChanged()
    }*/
}

