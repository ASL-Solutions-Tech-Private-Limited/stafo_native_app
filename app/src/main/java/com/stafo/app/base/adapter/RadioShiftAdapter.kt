package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RadioShiftItemLayoutBinding
import com.stafo.app.screens.settings.dataClass.ShiftDataList

class RadioShiftAdapter (
    private var shiftList: List<ShiftDataList>,
    var context: Context,
    var listener:ActionClickListener
) : RecyclerView.Adapter<RadioShiftAdapter.ViewHolder>() {
    private var selectedPosition = -1

    inner class ViewHolder(val binding: RadioShiftItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RadioShiftItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shiftData = shiftList[position]
        holder.binding.txtShiftName.text = shiftData.shift_name
        holder.binding.txtShiftTime.text = "Shift Time: ${shiftData.start_time+"-"+shiftData.end_time}"

        /*if (position == selectedPosition) {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_active_radio)
        } else {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg2)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_inactive_radio)
        }


        holder.itemView.setOnClickListener { listener.onActionClick(shiftData.id.toString()) }*/

        if (position == selectedPosition) {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_active_radio)
        } else {
            holder.binding.llShiftTime.setBackgroundResource(R.drawable.custom_switch_card_bg2)
            holder.binding.imgRadio.setImageResource(R.drawable.ic_lv_inactive_radio)
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            listener.onActionClick(shiftData.id.toString())
        }





    }

  /*  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(shiftList[position]) {
                binding.txtShiftName.text = this.shift_name

            }
        }
    }*/

    override fun getItemCount(): Int {
        return shiftList.size
    }

    interface ActionClickListener {
        fun onActionClick(action: String)

    }

}