package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemVehicleListBinding
import com.stafo.app.screens.tripPlan.dataClass.Vehicles
import com.stafo.app.utils.getIsCOMPANYLogin


class VehicleListAdapter(
    private val context: Context,
    private val vehicleList: List<Vehicles>,
    private val onItemClickListener: (Vehicles, String) -> Unit
) : RecyclerView.Adapter<VehicleListAdapter.FollowUpViewHolder>(), Filterable {

    private val filteredVehicleList = mutableListOf<Vehicles>()

    init {
        // Initialize filtered list with the full list
        filteredVehicleList.addAll(vehicleList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemVehicleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredVehicleList.size

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = filteredVehicleList[position]
        val binding = holder.binding

        // Set text values
        binding.tvVehicleNo.text = item.vehicleNo
        binding.tvTypeFuel.text = "${item.vehicleType} • ${item.fuel}"
        binding.tvLoadSpeed.text = "Load: ${item.loadCapacity}kg • Odometer: ${item.speedometer}km"
        binding.tvKmTravelled.text = "Total Travelled: ${item.speedometer}km"

        // Set status background tint
        val colorResId = if (item.status?.equals("Active", ignoreCase = true) == true) {
            R.color.green
        } else {
            R.color.pastel_red
        }
        binding.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, colorResId)

        if (item.status?.equals("Active", ignoreCase = true) == true) {
            binding.tvStatus.text = "Active"
        } else {
            binding.tvStatus.text = "Inactive"
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.reject))
        }

        // Visibility control
        binding.llEditDelete.visibility =
            if (getIsCOMPANYLogin(context)) View.VISIBLE else View.GONE

        // Set click listeners
        binding.root.setOnClickListener { onItemClickListener(item, "All") }
        binding.ivEdit.setOnClickListener { onItemClickListener(item, "Edit") }
        binding.ivDelete.setOnClickListener { onItemClickListener(item, "Delete") }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim() ?: ""
                val results = if (query.isEmpty()) {
                    vehicleList
                } else {
                    vehicleList.filter {
                        it.vehicleNo?.contains(query, ignoreCase = true) == true ||
                                it.vehicleType?.contains(query, ignoreCase = true) == true ||
                                it.fuel?.contains(query, ignoreCase = true) == true
                    }
                }
                return FilterResults().apply {
                    values = results
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredVehicleList.clear()
                filteredVehicleList.addAll(results?.values as? List<Vehicles> ?: emptyList())
                notifyDataSetChanged()
            }
        }
    }

    class FollowUpViewHolder(val binding: ItemVehicleListBinding) :
        RecyclerView.ViewHolder(binding.root)
}