package com.stafo.app.base.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyDownloadReportsItemLayoutBinding

class AdapterDownloadReports (
    //private var list: List<ActionModel>,
    var context: Activity
) : RecyclerView.Adapter<AdapterDownloadReports.ViewHolder>() {
    inner class ViewHolder(val binding: RecyDownloadReportsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyDownloadReportsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
           /* with(list[position]) {
                binding.tvName.text = this.name
                Glide.with(context).load(icon).into(binding.ivAction)
                itemView.setOnClickListener { listener.onActionClick(name) }
            }*/

        }
    }

    override fun getItemCount(): Int {
        return 4
    }


}