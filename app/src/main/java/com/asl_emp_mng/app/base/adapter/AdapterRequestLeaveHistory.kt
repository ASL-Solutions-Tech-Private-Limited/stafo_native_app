package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyCompanyLeaveHistoryItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
                binding.txtLeaveDate.text = this.fromDate + "-" + this.toDate
                binding.txtStartDate.text = this.fromDate
                binding.txtEndDate.text = this.toDate
                binding.txtAppliedDate.text = this.fromDate
                //binding.txtLeaveType.text = this.leaveType
                binding.txtLeaveStatus.text = this.status
                binding.txtDuration.text = calculateDuration(this.fromDate, this.toDate)

            }
        }
    }

    override fun getItemCount(): Int {
        return leavesManagementList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDuration(fromDate: String, toDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val fromDate = LocalDate.parse(fromDate, formatter)
        val toDate = LocalDate.parse(toDate, formatter)

        val daysBetween = ChronoUnit.DAYS.between(fromDate, toDate)
        return daysBetween.toString()
    }

}