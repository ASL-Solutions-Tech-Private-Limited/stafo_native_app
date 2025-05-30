package com.stafo.app.screens.bbps.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.bbps.dataClasses.DataPromo
import com.stafo.app.utils.copyTextFromTextView
import com.stafo.app.utils.isPromoExpired

class PromoCodeAdapter(private val context: Context, private val items: List<DataPromo>) :
    RecyclerView.Adapter<PromoCodeAdapter.PromoViewHolder>() {

    inner class PromoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label = view.findViewById<TextView>(R.id.labelText)
        val labelContainer = view.findViewById<LinearLayout>(R.id.label_container)
        val code = view.findViewById<TextView>(R.id.code)
        val offerLine = view.findViewById<TextView>(R.id.offerLine)
        val desc = view.findViewById<TextView>(R.id.offerDesc)
        val apply = view.findViewById<TextView>(R.id.apply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promo_code, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val item = items[position]
        holder.label.text = "₹${item.cashback} OFF"
        holder.labelContainer.background =
            if (isPromoExpired(item.expiresAt ?: "")) ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_label_red
            ) else ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_label_gray)


        if (isPromoExpired(item.expiresAt ?: "")) {
            ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_label_red
            )
            holder.apply.visibility = View.VISIBLE
            holder.apply.text = "COPY"
            holder.apply.isClickable = true
            holder.apply.isFocusable = true
            holder.apply.alpha = 1f
        } else {
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_label_gray)
            holder.apply.visibility = View.VISIBLE
            holder.apply.text = "Expired"
            holder.apply.isClickable = false
            holder.apply.isFocusable = false
            holder.apply.alpha = .5f
        }

        holder.code.text = item.code
        holder.offerLine.text = item.applicable
        holder.desc.text =
            "${item.type?.toUpperCase()} ₹${item.cashback} off on ${item.applicable?.toUpperCase()}.Maximum ₹${item.maxAmount}"
        holder.apply.setOnClickListener {
            copyTextFromTextView(context, holder.code)
        }
    }

    override fun getItemCount(): Int = items.size
}
