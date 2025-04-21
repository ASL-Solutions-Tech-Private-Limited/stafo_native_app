package com.stafo.app.screens.recharge.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.CouponItemLayoutBinding
import com.stafo.app.databinding.RecentRechargeItemLayoutBinding

class CouponAdapter (
    var context: Activity
) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CouponItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CouponItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            /*  with(contactList[position]) {
                  binding.tvName.text = this.name
                  binding.tvPhone.text = this.phone


                  val placeholderBitmap = generateTextBitmap(this.name ?: "?")
                  Glide.with(context)
                      .load(placeholderBitmap)
                      .into(binding.sivTitle)


                  holder.itemView.setOnClickListener {
                      context.startActivity(Intent(context, PlanActivity::class.java))
                  }


              }*/
        }
    }

    override fun getItemCount(): Int {
        return 4
    }



}