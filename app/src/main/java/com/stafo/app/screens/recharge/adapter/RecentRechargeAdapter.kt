package com.stafo.app.screens.recharge.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecentRechargeItemLayoutBinding

class RecentRechargeAdapter (
    var context: Activity
) : RecyclerView.Adapter<RecentRechargeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecentRechargeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentRechargeItemLayoutBinding.inflate(
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
        return 1
    }



}