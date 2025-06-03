package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemVehicleListBinding
import com.stafo.app.screens.tripPlan.dataClass.Vehicles
import com.stafo.app.utils.generateGradientDrawables


class VehicleListAdapter(
    private var mContext: Context,
    private var mVehicleList: List<Vehicles>,
    private val onItemClickListener: (Vehicles) -> Unit
) : RecyclerView.Adapter<VehicleListAdapter.FollowUpViewHolder>() {
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemVehicleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = mVehicleList[position]
        val binding = holder.binding

        binding.tvVehicleNo.text = item.vehicleNo
        binding.tvTypeFuel.text = "${item.vehicleType} • ${item.fuel}"
        binding.tvLoadSpeed.text = "Load: ${item.loadCapacity}kg • Odometer: ${item.speedometer}km"
        binding.tvKmTravelled.text = "Total Travelled: ${item.speedometer}km"
        binding.root.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = mVehicleList.size

    class FollowUpViewHolder(val binding: ItemVehicleListBinding) :
        RecyclerView.ViewHolder(binding.root)
}