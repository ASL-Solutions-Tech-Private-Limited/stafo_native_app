package com.stafo.app.screens.bbps.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.BuildConfig
import com.stafo.app.R
import com.stafo.app.screens.bbps.BBPSDashboard.ServiceItem
import com.stafo.app.screens.bbps.dataClasses.DataCategory

class BBPSCategoriesAdapter(
    private val context: Context,
    private val isCategoryList: Boolean,
    private val services: List<ServiceItem>?,
    private val categories: List<DataCategory>?,
    private val onServiceClicked: (ServiceItem) -> Unit,
    private val onCategoryClicked: (DataCategory) -> Unit
) : RecyclerView.Adapter<BBPSCategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bbps_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isCategoryList) {
            categories?.get(position)?.let { holder.bindCategory(it) }
        } else {
            services?.get(position)?.let { holder.bindService(it) }
        }
    }

    override fun getItemCount(): Int {
        return if (isCategoryList) categories?.size ?: 0 else services?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.serviceIcon)
        private val name: TextView = itemView.findViewById(R.id.serviceName)

        fun bindService(service: ServiceItem) {
            name.text = service.name
            icon.setImageResource(service.iconRes)
            /*try {
                val color = Color.parseColor(service.color)
                icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } catch (e: Exception) {
                icon.setColorFilter(Color.parseColor("#6366F1"), PorterDuff.Mode.SRC_IN)
            }*/

            itemView.setOnClickListener { onServiceClicked(service) }
        }

        fun bindCategory(category: DataCategory) {
            name.text = category.name
            Glide.with(context)
                .load(BuildConfig.ENDPOINT + category.categoryIcon)
                .placeholder(R.drawable.ic_bbps_ic)
                .into(icon)
            icon.clearColorFilter()

            itemView.setOnClickListener { onCategoryClicked(category) }
        }
    }
}
