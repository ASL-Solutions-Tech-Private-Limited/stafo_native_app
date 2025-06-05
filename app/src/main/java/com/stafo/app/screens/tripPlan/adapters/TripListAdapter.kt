package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemTripHistoryBinding
import com.stafo.app.screens.tripPlan.dataClass.dashboard.Trips
import com.stafo.app.utils.generateGradientDrawables
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.getSmartShortAddress


class TripListAdapter(
    private var mContext: Context,
    private var tripData: List<Trips>,
    private val onItemClickListener: (Trips, String) -> Unit
) : RecyclerView.Adapter<TripListAdapter.FollowUpViewHolder>() {
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemTripHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = tripData[position]
        val binding = holder.binding

        binding.tvTripDate.text = item.startTime
        binding.tvTripFromTo.text =
            getSmartShortAddress(item.fromAddress) + " ➝ " + getSmartShortAddress(item.toAddress)
        binding.tvDistance.text = "Distance: " + item.distance + " km"
        binding.tvDuration.text = "Duration: 00:00"
        binding.tvOdometer.text = "Odometer:00000" + " ➝ " + "00000"
        binding.tvExpenses.text = "Expenses: ₹000"
        binding.cardRide.background = gradientBackgrounds[position]
        binding.tvStatus.text = item.status ?: "Pending"
        if (item.status == "Completed") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.green)
        } else if (item.status == "Pending") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.pending_colour)

        } else if (item.status == "Cancelled") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.pastel_red)
        } else if (item.status == "Ongoing") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.xp_blue)
        }


        if (getIsCOMPANYLogin(mContext)) binding.llEditDelete.visibility = View.VISIBLE
        else binding.llEditDelete.visibility = View.GONE
        binding.ivEdit.setOnClickListener {
            onItemClickListener(item, "Edit")
        }
        binding.ivDelete.setOnClickListener {
            onItemClickListener(item, "Delete")
        }
        binding.root.setOnClickListener {
            onItemClickListener(item, "All")
        }
    }

    override fun getItemCount(): Int = tripData.size

    class FollowUpViewHolder(val binding: ItemTripHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}