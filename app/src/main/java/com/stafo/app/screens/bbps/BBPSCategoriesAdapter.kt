package com.stafo.app.screens.bbps

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.bbps.BBPSDashboard.ServiceItem


class BBPSCategoriesAdapter(
    private val services: List<ServiceItem>,
    private var onItemClicked: (ServiceItem) -> Unit
) :
    RecyclerView.Adapter<BBPSCategoriesAdapter.ServiceViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bbps_category, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    inner class ServiceViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceIcon: ImageView = itemView.findViewById<ImageView>(R.id.serviceIcon)
        private val serviceName: TextView = itemView.findViewById<TextView>(R.id.serviceName)

        init {
            itemView.setOnClickListener { v: View? ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked.invoke(services[adapterPosition])
                }
            }
        }

        fun bind(service: ServiceItem) {
            serviceName.text = service.name
            serviceIcon.setImageResource(service.iconRes)


            // Set icon tint color
            try {
                val color = Color.parseColor(service.color)
                serviceIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } catch (e: IllegalArgumentException) {
                // If color parsing fails, use default color
                serviceIcon.setColorFilter(Color.parseColor("#6366F1"), PorterDuff.Mode.SRC_IN)
            }
        }
    }
}
