package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemDriverLsitBinding
import com.stafo.app.screens.tripPlan.dataClass.Drivers
import com.stafo.app.utils.generateGradientDrawables


class DriverListAdapter(
    private var mContext: Context,
    private var mVehicleList: List<Drivers>,
    private val onItemClickListener: (Drivers) -> Unit
) : RecyclerView.Adapter<DriverListAdapter.FollowUpViewHolder>() {
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemDriverLsitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = mVehicleList[position]
        val binding = holder.binding

        binding.tvName.text = item.name
        binding.tvPosition.text = item.position
        binding.tvEmpId.text = item.empId

        binding.root.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = mVehicleList.size

    class FollowUpViewHolder(val binding: ItemDriverLsitBinding) :
        RecyclerView.ViewHolder(binding.root)
}