package com.stafo.app.screens.bbps.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.bbps.dataClasses.DataBiller

class BBPSBillersAdapter(
    private val context: Context,
    private val onCategoryClicked: (DataBiller) -> Unit
) : RecyclerView.Adapter<BBPSBillersAdapter.ViewHolder>() {

    private var originalList: List<DataBiller> = emptyList()
    private var filteredList: MutableList<DataBiller> = mutableListOf()

    fun setData(newList: List<DataBiller>) {
        originalList = newList
        filteredList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredList = if (lowerCaseQuery.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name?.lowercase()?.contains(lowerCaseQuery) == true
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bbps_billers, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filteredList.getOrNull(position)?.let { holder.bindService(it) }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.serviceIcon)
        private val name: TextView = itemView.findViewById(R.id.serviceName)

        fun bindService(service: DataBiller) {
            name.text = service.name
            itemView.setOnClickListener { onCategoryClicked(service) }
        }
    }

    fun getAllData(): List<DataBiller> {
        return filteredList
    }
}

