package com.stafo.app.screens.rank.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemPointsLayoutBinding
import com.stafo.app.screens.rank.dataClass.PerformanceRecord

class AdapterPoints (
    private var list: List<PerformanceRecord>,
    var context: Activity
) : RecyclerView.Adapter<AdapterPoints.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPointsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPointsLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvPointName.text = this.performancetype.name
                binding.tvPoints.text = this.marks.toString()

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateData(newList: List<PerformanceRecord>) {
        this.list = newList
        notifyDataSetChanged()
    }


}