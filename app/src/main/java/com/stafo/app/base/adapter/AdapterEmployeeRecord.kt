package com.stafo.app.base.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.DateItem
import com.stafo.app.databinding.RecyEmpAttendanceChildLayoutBinding
import com.stafo.app.screens.emp.EmpDayAttendanceRecordActivity
import com.stafo.app.utils.calculateHours2
import com.stafo.app.utils.extractDayNameDateAndMonth
import com.stafo.app.utils.extractDayNameDateAndMonth2
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AdapterEmployeeRecord(
    private val context: Context,
    private val employeeId: String
) : ListAdapter<DateItem, AdapterEmployeeRecord.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: RecyEmpAttendanceChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyEmpAttendanceChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

   /* @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvDay.text = extractDayNameDateAndMonth(item.date).first
            tvDate.text = extractDayNameDateAndMonth(item.date).second.toString()
            llcAttend.visibility = View.VISIBLE




         *//*   if (item.isPresent == "Absent") {
                llcAttend.visibility = View.GONE
                llcWeekOff.visibility = View.VISIBLE
                tvWeekOffDay.text = extractDayNameDateAndMonth(item.date).first
                tvWeekOffDate.text = extractDayNameDateAndMonth(item.date).second.toString()
            } else {
                llcAttend.visibility = View.VISIBLE
              *//**//*  llcWeekOff.visibility = View.GONE
                tvCheckIn.text = item.punchIn
                tvCheckOut.text = if (item.punchOut == "null") "" else item.punchOut

                tvWorkingHrs.text =
                    if (!item.punchIn.isNullOrEmpty() && !item.punchOut.isNullOrEmpty()) {
                        calculateHours2(item.punchIn, item.punchOut)
                    } else {
                        ""
                    }*//**//*
            }*//*

            holder.itemView.setOnClickListener {

                val intent = Intent(context, EmpDayAttendanceRecordActivity::class.java).apply {
                    putExtra("EMP_ID", employeeId)
                }

                context.startActivity(intent)
            }

            val storeDate = item.date
            if (storeDate == getCurrentDate()) {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light_400))
            } else {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }*/


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            if (item.isPlaceholder) {
                llcAttend.visibility = View.INVISIBLE
                clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                return
            }

            // Extract day name and day of month
            val (dayName, dayOfMonth) = extractDayNameDateAndMonth2(item.date)

            tvDate.text = dayOfMonth
            llcAttend.visibility = View.VISIBLE
            rtlDate.setBackgroundResource(R.drawable.custom_date_bg)
            when (item.isPresent) {
                "Absent" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_absent_bg)
                }
                "Present" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_present_bg)
                }
            }

            if (dayName == "Sun") {
                Log.d("date", "get value: $dayName date :${item.date}")
                rtlDate.setBackgroundResource(R.drawable.custom_sunday_bg)
            }

            holder.itemView.setOnClickListener {

                val intent = Intent(context, EmpDayAttendanceRecordActivity::class.java).apply {
                    putExtra("EMP_ID", employeeId)
                    putExtra("select_date", item.date)
                }
                context.startActivity(intent)
            }

            val storeDate = item.date
            if (storeDate == getCurrentDate()) {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_red_light2))
            } else {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }

        }
    }


    class DiffCallback : DiffUtil.ItemCallback<DateItem>() {
        override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem == newItem
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDatePosition(): Int {
        return currentList.indexOfFirst { it.date == getCurrentDate() }
    }
}


