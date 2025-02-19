package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.model.DateItem
import com.asl_emp_mng.app.databinding.RecyEmpAttendanceChildLayoutBinding
import com.asl_emp_mng.app.utils.calculateHours
import com.asl_emp_mng.app.utils.extractDayNameDateAndMonth
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterEmployeeRecord(
    private var list: List<DateItem>,
    var context: Context
) : RecyclerView.Adapter<AdapterEmployeeRecord.ViewHolder>() {
    inner class ViewHolder(val binding: RecyEmpAttendanceChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyEmpAttendanceChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvDay.text = extractDayNameDateAndMonth(this.date).first
                binding.tvDate.text = extractDayNameDateAndMonth(this.date).second.toString()
                if (this.isPresent == "Absent") {
                    binding.llcAttend.visibility = View.GONE
                    binding.llcWeekOff.visibility = View.VISIBLE
                    binding.tvWeekOffDay.text = extractDayNameDateAndMonth(this.date).first
                    binding.tvWeekOffDate.text = extractDayNameDateAndMonth(this.date).second.toString()

                } else if (this.isPresent == "Present") {
                    binding.llcAttend.visibility = View.VISIBLE
                    binding.llcWeekOff.visibility = View.GONE
                    binding.tvCheckIn.text = this.punchIn
                    binding.tvCheckOut.text = this.punchOut
                    binding.tvWorkingHrs.text = calculateHours(this.punchIn, this.punchOut)
                } else {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }




}