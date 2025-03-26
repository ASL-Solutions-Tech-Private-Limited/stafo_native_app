package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.stafo.app.R
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.stafo.app.screens.settings.dataClass.BannerData

class SliderAdapter(private val context: Context,var sliderItems: List<BannerData>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {



    // Creating ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    // Bind the data to each view (ImageView)
    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem = sliderItems[position]

        val imageUrl="${sliderItem.path}/${sliderItem.banner[position].image}"

        Glide.with(viewHolder.itemView)
            .load(imageUrl)
            .fitCenter()
            .into(viewHolder.imageViewBackground)

        viewHolder.itemView.setOnClickListener {
            // Add any click event logic here if needed
        }
    }

    // Get item count
    override fun getCount(): Int {
        return sliderItems.size
    }

    // ViewHolder class for each slider item
    inner class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        val imageGifContainer: ImageView = itemView.findViewById(R.id.iv_gif_container)
        val textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)
    }
}
