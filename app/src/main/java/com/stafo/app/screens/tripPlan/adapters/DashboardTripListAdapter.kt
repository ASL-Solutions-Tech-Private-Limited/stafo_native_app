package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemTripHistoryBinding
import com.stafo.app.screens.tripPlan.dataClass.TripsData
import com.stafo.app.utils.formatAmount
import com.stafo.app.utils.generateGradientDrawables
import com.stafo.app.utils.getSmartShortAddress
import java.util.Locale


class DashboardTripListAdapter(
    private var mContext: Context,
    private var tripData: List<TripsData>,
    private val onItemClickListener: (TripsData) -> Unit
) : RecyclerView.Adapter<DashboardTripListAdapter.FollowUpViewHolder>() {
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
        binding.tvTripFromTo.text = getSmartShortAddress(item.fromAddress?:"") + " ➝ " + getSmartShortAddress(item.toAddress?:"")
        binding.tvDistance.text = "Distance: " + item.distance + " km"
        binding.tvDuration.text = "Duration:${item.duration ?: "00:00"}"
        binding.tvOdometer.text =
            "Odometer:${item.odometer_start ?: "00000"}" + " ➝ " + "${item.odometer_latest ?: "00000"}"
        binding.tvExpenses.text =
            "Expenses: ${formatAmount(item?.total_expenses?.toDouble() ?: 0.0)}"
        binding.cardRide.background = gradientBackgrounds[position]
        binding.tvStatus.text = item.status ?: "Pending"
        if (item.status?.lowercase(Locale.ROOT) == "completed") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.green)
            binding.ivEdit.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
        } else if (item.status?.lowercase(Locale.ROOT) == "pending") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.pending_colour)
            binding.ivEdit.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE
        } else if (item.status?.lowercase(Locale.ROOT) == "cancelled") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.pastel_red)
            binding.ivEdit.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
        } else if (item.status?.lowercase(Locale.ROOT) == "ongoing") {
            binding.tvStatus.backgroundTintList =
                holder.itemView.resources.getColorStateList(R.color.xp_blue)
            binding.ivEdit.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
        }
        binding.root.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = tripData.size

    class FollowUpViewHolder(val binding: ItemTripHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)



}