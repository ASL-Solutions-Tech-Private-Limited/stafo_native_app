package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyEmpAttendanceChildLayoutBinding
import com.asl_emp_mng.app.screens.settings.EmployeeAttendanceRecordActivity
import com.asl_emp_mng.app.screens.settings.dataClass.AttendanceRecord
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeRecord
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmpAttendanceRecord

class AdapterEmployeeRecord(
    private var list: List<AttendanceRecord>,
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

                if (this.attendance == "Absent") {
                    binding.llcAttend.visibility = View.GONE
                    binding.llcWeekOff.visibility = View.VISIBLE
                    var day=(context as EmployeeAttendanceRecordActivity).getDayNameOld(this.date)
                    binding.tvWeekOffDay.text=day

                    var date=(context as EmployeeAttendanceRecordActivity).getDate(this.date)
                    binding.tvWeekOffDate.text=date
                } else {
                    binding.llcAttend.visibility = View.VISIBLE
                    binding.llcWeekOff.visibility = View.GONE
                    binding.tvCheckIn.text = this.in_time
                    binding.tvCheckOut.text = this.out_time
                    var day=(context as EmployeeAttendanceRecordActivity).getDayNameOld(this.date)
                    binding.tvDay.text=day
                    var date=(context as EmployeeAttendanceRecordActivity).getDate(this.date)
                    binding.tvDate.text=date
                }



            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}