package com.stafo.app.screens.crm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemFollowUpListBinding
import com.stafo.app.utils.generateGradientDrawables


class FollowUpAdapter(

) : RecyclerView.Adapter<FollowUpAdapter.FollowUpViewHolder>() {

    // Pre-generated gradient list (one per item)
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemFollowUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        //    val item = followUpList[position]
        val binding = holder.binding


        // Set background
        binding.cardView.background = gradientBackgrounds[position % gradientBackgrounds.size]

        // Set text color to white for all text views
        binding.tvLeadName.setTextColor(Color.WHITE)
        binding.tvLeadCompany.setTextColor(Color.WHITE)
        binding.tvFollowUpNote.setTextColor(Color.WHITE)
        binding.tvFollowUpDate.setTextColor(Color.WHITE)
    }

    override fun getItemCount(): Int = 4

    class FollowUpViewHolder(val binding: ItemFollowUpListBinding) :
        RecyclerView.ViewHolder(binding.root)
}