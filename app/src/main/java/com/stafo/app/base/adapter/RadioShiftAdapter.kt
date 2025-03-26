package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RadioShiftItemLayoutBinding
import com.stafo.app.screens.settings.dataClass.ShiftDataList

class RadioShiftAdapter(
    private var shiftList: List<ShiftDataList>,
    var context: Context,
    var listener: ActionClickListener
) : RecyclerView.Adapter<RadioShiftAdapter.ViewHolder>() {

    private var selectedItems = mutableSetOf<Int>()
    private var selectedShiftIds = mutableSetOf<String>()
    private var isMultiSelectionEnabled = false

    inner class ViewHolder(val binding: RadioShiftItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RadioShiftItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shiftData = shiftList[position]
        holder.binding.txtShiftName.text = shiftData.shift_name
        holder.binding.txtShiftTime.text =
            "Shift Time: ${shiftData.start_time} - ${shiftData.end_time}"
        val isSelected = selectedItems.contains(position)

        if (isSelected) {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_active_radio)
        } else {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg2)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_inactive_radio)
        }

        holder.itemView.setOnClickListener {
            if (isMultiSelectionEnabled) {
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position)
                    selectedShiftIds.remove(shiftData.id.toString())
                } else {
                    selectedItems.add(position)
                    selectedShiftIds.add(shiftData.id.toString())
                }
            } else {
                selectedItems.clear()
                selectedShiftIds.clear()
                selectedItems.add(position)
                selectedShiftIds.add(shiftData.id.toString())
            }
            notifyDataSetChanged()
            listener.onActionClick(getSelectedShiftIds())
        }
    }

    override fun getItemCount(): Int {
        return shiftList.size
    }

    fun setMultiSelectionEnabled(enabled: Boolean) {
        isMultiSelectionEnabled = enabled
        selectedItems.clear()
        selectedShiftIds.clear()

        if (enabled) {
            shiftList.indices.forEach { index ->
                selectedItems.add(index)
                selectedShiftIds.add(shiftList[index].id.toString())
            }
        }
        notifyDataSetChanged()
        listener.onActionClick(getSelectedShiftIds())
    }

    fun getSelectedShiftIds(): List<String> {
        return selectedShiftIds.toList()
    }

    interface ActionClickListener {
        fun onActionClick(selectedShifts: List<String>)
    }
}
