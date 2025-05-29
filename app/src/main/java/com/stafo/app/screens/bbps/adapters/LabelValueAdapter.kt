package com.stafo.app.screens.bbps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.bbps.BillDetailsAndPaymentActivity

class LabelValueAdapter(private val items: List<BillDetailsAndPaymentActivity.LabelValuePair>) :
    RecyclerView.Adapter<LabelValueAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
        val tvValue: TextView = itemView.findViewById(R.id.tvValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill_details_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = items[position]
        holder.tvLabel.text = pair.label
        holder.tvValue.text = pair.value
    }
}
