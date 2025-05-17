package com.stafo.app.screens.crm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemLeadsListBinding
import com.stafo.app.screens.crm.dataClass.LeadData
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.getFormatDate

// Lead Adapter to display the list of leads
class LeadAdapter(
    private var leadList: List<LeadData>,
    private var onFollowUpClick: (LeadData) -> Unit,
    private var onEditClick: (LeadData) -> Unit
) : RecyclerView.Adapter<LeadAdapter.LeadViewHolder>() {

    inner class LeadViewHolder(val binding: ItemLeadsListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadViewHolder {
        val binding =
            ItemLeadsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeadViewHolder, position: Int) {
        val lead = leadList[position]

        holder.binding.tvName.text = lead.name
        holder.binding.tvCompany.text = lead.company?.company_name
        holder.binding.tvPhone.text = lead.phone.toString()
        holder.binding.tvStatus.text = lead.status
        holder.binding.tvlastFollowup.text = lead.next_date?.let { getFormatDate(it) }

        holder.binding.btnFollowUp.setOnClickListener {
            onFollowUpClick.invoke(lead)
        }

        holder.binding.btnEdit.setOnClickListener {
            onEditClick.invoke(lead)
        }
    }

    override fun getItemCount(): Int = leadList.size
}

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