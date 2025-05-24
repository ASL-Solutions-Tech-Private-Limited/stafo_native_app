package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyShiftTimeChildLayoutBinding
import com.stafo.app.screens.settings.AddShiftActivity
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
                binding.txtShiftName.text = this.shift_name
                binding.txtShiftTime.text = this.start_time+"-"+this.end_time

                val activeDays = mutableListOf<String>()
                if (sunday == 1) activeDays.add("Sun")
                if (monday == 1) activeDays.add("Mon")
                if (tuesday == 1) activeDays.add("Tue")
                if (wednesday == 1) activeDays.add("Wed")
                if (thursday == 1) activeDays.add("Thu")
                if (friday == 1) activeDays.add("Fri")
                if (saturday == 1) activeDays.add("Sat")

                binding.tvDays.text = activeDays.joinToString(", ")

                binding.itemDelete.setOnClickListener {
                    (context as AddShiftActivity).deleteShift(this.id)
                }
                binding.itemEdit.setOnClickListener {
                    (context as AddShiftActivity).showCustomBottomSheet("Edit",this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return shiftList.size
    }

    fun updateList(newList: List<ShiftDataList>) {
        shiftList = newList
        notifyDataSetChanged()
    }

}