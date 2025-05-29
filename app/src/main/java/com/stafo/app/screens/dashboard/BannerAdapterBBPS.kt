package com.stafo.app.screens.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemBannerDdBinding
import com.stafo.app.screens.bbps.BBPSDashboard
import com.stafo.app.utils.generateGradientDrawables

class BannerAdapterBBPS(private val items: List<BBPSDashboard.BannerItem>) :
    RecyclerView.Adapter<BannerAdapterBBPS.BannerViewHolder>() {
    private val gradientBackgrounds = generateGradientDrawables(items.size)
    inner class BannerViewHolder(val binding: ItemBannerDdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerDdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = items[position]
        holder.binding.bannerTitle.text = item.title
        holder.binding.bannerSubtitle.text = item.subtitle
        holder.binding.bannerImage.setImageResource(item.iconRes)
        holder.binding.bannerContainer.background =
            gradientBackgrounds[position % gradientBackgrounds.size]
    }

    override fun getItemCount(): Int = items.size
}
