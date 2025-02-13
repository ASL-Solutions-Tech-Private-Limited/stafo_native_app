package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyHolidayItemLayoutBinding

class AdapterHoliday (
    //private var leavesManagementList: List<LeavesManagementModel>,
    var context: Context
) : RecyclerView.Adapter<AdapterHoliday.ViewHolder>() {
    inner class ViewHolder(val binding: RecyHolidayItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyHolidayItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /* with(holder) {
             with(leavesManagementList[position]) {
                 binding.txtEmpName.text = this.empName
                 binding.txtLeaveDate.text = this.leaveDate
                 binding.txtInfo.setOnClickListener {
                     (context as LeaveManagementActivity).showCustomBottomSheet()
                 }
             }
         }*/
    }

    override fun getItemCount(): Int {
        return 6
    }

}