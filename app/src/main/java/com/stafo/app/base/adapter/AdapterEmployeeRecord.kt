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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)

        with(holder.binding) {

            // 🔹 Placeholder (empty grid cells)
            if (item.isPlaceholder) {
                llcAttend.visibility = View.INVISIBLE
                clLayout.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                return
            }

            llcAttend.visibility = View.VISIBLE

            // 🔹 Extract date info
            val (dayName, dayOfMonth) = extractDayNameDateAndMonth2(item.date)
            tvDate.text = dayOfMonth

            // 🔹 Default background
            rtlDate.setBackgroundResource(R.drawable.custom_date_bg)

            // 🔹 Status-based UI
            when (item.isPresent) {

                "Present" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_present_bg)
                }

                "Absent" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_absent_bg)
                }

                "Week Off" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_weekoff_bg)
                }

                "Holiday" -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_holiday_bg)
                }

                else -> {
                    rtlDate.setBackgroundResource(R.drawable.custom_date_bg)
                }
            }

            // 🔹 Click handling (only for valid working days)
            if (item.isPresent == "Present" || item.isPresent == "Absent") {
                holder.itemView.setOnClickListener {

                    val intent = Intent(context, EmpDayAttendanceRecordActivity::class.java).apply {
                        putExtra("EMP_ID", employeeId)
                        putExtra("select_date", item.date)
                    }

                    context.startActivity(intent)
                }
            } else {
                holder.itemView.setOnClickListener(null)
            }

            // 🔹 Highlight Today
            if (item.date == getCurrentDate()) {
                clLayout.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.pastel_red_light2)
                )
            } else {
                clLayout.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )
            }
        }
    }

    // 🔹 DiffUtil
    class DiffCallback : DiffUtil.ItemCallback<DateItem>() {
        override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem == newItem
        }
    }

    // 🔹 Get current date
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    // 🔹 Scroll to current date
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDatePosition(): Int {
        return currentList.indexOfFirst { it.date == getCurrentDate() }
    }
}


