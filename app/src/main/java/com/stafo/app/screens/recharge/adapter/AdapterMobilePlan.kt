package com.stafo.app.screens.recharge.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.recharge.PayRechargeActivity
import com.stafo.app.screens.recharge.dataclass.RechargeInfo

class AdapterMobilePlan (val context:Activity,private val items: List<RechargeInfo>) : RecyclerView.Adapter<AdapterMobilePlan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val price: TextView = itemView.findViewById(R.id.txt_amount)
        val details: TextView = itemView.findViewById(R.id.txt_third_value)
        val validity: TextView = itemView.findViewById(R.id.txt_first_value)
        val offer: TextView = itemView.findViewById(R.id.txt_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recharge_plan_item_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.price.text = "\u20B9 "+item.price
        holder.details.text = item.details
        holder.validity.text = item.validity
        holder.offer.text = item.offer

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,PayRechargeActivity::class.java))
        }
    }

    override fun getItemCount(): Int = items.size
}