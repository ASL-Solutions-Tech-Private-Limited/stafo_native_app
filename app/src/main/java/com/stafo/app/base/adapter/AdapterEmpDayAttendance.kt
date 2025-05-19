package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyDayAttendanceRecordBinding
import com.stafo.app.screens.settings.dataClass.PunchData
import com.stafo.app.utils.convertTo12HourFormat2
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterEmpDayAttendance (
    private var list: List<PunchData>,
    var context: Context
) : RecyclerView.Adapter<AdapterEmpDayAttendance.ViewHolder>() {
    inner class ViewHolder(val binding: RecyDayAttendanceRecordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyDayAttendanceRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                binding.txtAttendanceInTime.text=convertTo12HourFormat2(this.punch_in)
                binding.txtAttendanceOutTime.text=convertTo12HourFormat2(this.punch_out)



            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateList(newList: List<PunchData>) {
        list = newList
        notifyDataSetChanged()
    }





}