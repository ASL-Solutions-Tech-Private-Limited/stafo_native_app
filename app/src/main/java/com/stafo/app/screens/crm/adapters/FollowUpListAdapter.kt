package com.stafo.app.screens.crm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemFollowUpListBinding
import com.stafo.app.screens.crm.dataClass.FollowUpItem
import com.stafo.app.screens.crm.dataClass.TotalFollowupsToday
import com.stafo.app.utils.generateGradientDrawables
import com.stafo.app.utils.getFormatDate

class FollowUpListAdapter (
    private var followUpList: List<FollowUpItem>,
    ) : RecyclerView.Adapter<FollowUpListAdapter.FollowUpViewHolder>() {

    // Pre-generated gradient list (one per item)
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemFollowUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = followUpList[position]
        val binding = holder.binding

        binding.tvLeadName.text=item.employee.name
        binding.tvLeadCompany.text=item.company?.company_name
        binding.tvFollowUpNote.text=item.remarks
        binding.tvFollowUpDate.text= getFormatDate(item.next_date)






        // Set background
        binding.cardView.background = gradientBackgrounds[position % gradientBackgrounds.size]

        // Set text color to white for all text views
        binding.tvLeadName.setTextColor(Color.WHITE)
        binding.tvLeadCompany.setTextColor(Color.WHITE)
        binding.tvFollowUpNote.setTextColor(Color.WHITE)
        binding.tvFollowUpDate.setTextColor(Color.WHITE)
    }

    override fun getItemCount(): Int = followUpList.size

    class FollowUpViewHolder(val binding: ItemFollowUpListBinding) :
        RecyclerView.ViewHolder(binding.root)
}