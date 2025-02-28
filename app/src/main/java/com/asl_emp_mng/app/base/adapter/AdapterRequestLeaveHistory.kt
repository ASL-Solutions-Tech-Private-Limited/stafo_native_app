package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.RecyCompanyLeaveHistoryItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveData
import com.asl_emp_mng.app.utils.getFormatDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class AdapterRequestLeaveHistory(
    private var leavesManagementList: List<LeaveData>,
    var context: Context
) : RecyclerView.Adapter<AdapterRequestLeaveHistory.ViewHolder>() {
    inner class ViewHolder(val binding: RecyCompanyLeaveHistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyCompanyLeaveHistoryItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(leavesManagementList[position]) {
                binding.txtEmpName.text = this.employeeBasicInfo.name
                binding.txtLeaveDate.text = "${getFormatDate(this.fromDate)} - ${getFormatDate(this.toDate)}"
                binding.txtStartDate.text = "${getFormatDate(this.fromDate)}"
                binding.txtEndDate.text = "${getFormatDate(this.toDate)}"
                binding.txtAppliedDate.text = "${getFormatDate(this.fromDate)}"

                if (this.leaveType=="1"){
                    binding.txtLeaveType.text="Casual Leave"
                }else if (this.leaveType=="2"){
                    binding.txtLeaveType.text="Sick Leave"
                }else{
                    binding.txtLeaveType.text="Previllage Leave"
                }

                //binding.txtLeaveType.text = this.leaveType
                if (this.status=="approved"){
                    binding.txtLeaveStatus.setTextColor(context.getColor(R.color.primaryColor))
                }else if (this.status=="rejected"){
                    binding.txtLeaveStatus.setTextColor(context.getColor(R.color.reject))
                }else{
                    binding.txtLeaveStatus.setTextColor(context.getColor(R.color.reject))
                }

                binding.txtLeaveStatus.text = this.status.replaceFirstChar { it.uppercase() }
                binding.txtDuration.text = calculateDuration(this.fromDate, this.toDate)

            }
        }
    }

    override fun getItemCount(): Int {
        return leavesManagementList.size
    }


    fun updateList(newList: List<LeaveData>) {
        leavesManagementList = newList
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDuration(fromDate: String, toDate: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDateParsed = LocalDate.parse(fromDate.trim(), formatter)
            val toDateParsed = LocalDate.parse(toDate.trim(), formatter)
            // val daysBetween = ChronoUnit.DAYS.between(fromDateParsed, toDateParsed)

            val daysBetween = ChronoUnit.DAYS.between(fromDateParsed, toDateParsed) + 1
            /*  daysBetween.toString()
              if (daysBetween == 0L) {
                  return "1 day"
              }*/

            "$daysBetween"

        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }




}