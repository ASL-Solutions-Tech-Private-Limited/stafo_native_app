package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyShiftTimeChildLayoutBinding
import com.stafo.app.screens.settings.dataClass.ShiftDataList

class ShiftAdapter(
    private var shiftList: List<ShiftDataList>,
    var context: Context
) : RecyclerView.Adapter<ShiftAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyShiftTimeChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyShiftTimeChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(shiftList[position]) {
                binding.tvShiftName.text = this.shift_name
                binding.tvShiftTime.text = this.start_time+"-"+this.end_time
            }
        }
    }

    override fun getItemCount(): Int {
        return shiftList.size
    }

}