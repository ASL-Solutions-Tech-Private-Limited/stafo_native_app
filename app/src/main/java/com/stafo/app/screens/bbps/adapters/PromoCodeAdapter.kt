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

    class PromoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.labelText)
        val labelContainer: LinearLayout = view.findViewById(R.id.label_container)
        val code: TextView = view.findViewById(R.id.code)
        val offerLine: TextView = view.findViewById(R.id.offerLine)
        val desc: TextView = view.findViewById(R.id.offerDesc)
        val apply: TextView = view.findViewById(R.id.apply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promo_code, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val item = items[position]
        holder.label.text = "₹${item.cashback} OFF"
        
        val expired = isPromoExpired(item.expiresAt ?: "")

        holder.labelContainer.background = ContextCompat.getDrawable(
            holder.itemView.context,
            if (expired) R.drawable.bg_label_gray else R.drawable.bg_label_red,
        )

        if (expired) {
            holder.apply.visibility = View.VISIBLE
            holder.apply.text = "Expired"
            holder.apply.isClickable = false
            holder.apply.isFocusable = false
            holder.apply.alpha = .5f
        } else {
            holder.apply.visibility = View.VISIBLE
            holder.apply.text = "COPY"
            holder.apply.isClickable = true
            holder.apply.isFocusable = true
            holder.apply.alpha = 1f
        }

        holder.code.text = item.code
        holder.offerLine.text = item.applicable
        holder.desc.text =
            "${item.type?.uppercase()} ₹${item.cashback} off on ${item.applicable?.uppercase()}.Maximum ₹${item.maxAmount}"
        holder.apply.setOnClickListener {
            if (!expired) {
                copyTextFromTextView(context, holder.code)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
