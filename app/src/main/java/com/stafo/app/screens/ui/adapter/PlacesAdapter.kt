package com.stafo.app.screens.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlacesAdapter(
    private val onPlaceClick: (String) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private var items = listOf<AutocompletePrediction>()

    fun submitList(data: List<AutocompletePrediction>) {
        items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item.getFullText(null).toString(), item.placeId)
    }

    override fun getItemCount() = items.size

    inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)

        fun bind(name: String, placeId: String) {
            textView.text = name
            itemView.setOnClickListener {
                onPlaceClick(placeId)
            }
        }
    }
}
