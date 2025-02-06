package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.model.LeavesManagementModel
import com.asl_emp_mng.app.databinding.RecyLeaveManagementChildLayoutBinding

class LeavesManagementAdapter(
    private var leavesManagementList: List<LeavesManagementModel>,
    var context: Context
) : RecyclerView.Adapter<LeavesManagementAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyLeaveManagementChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyLeaveManagementChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(leavesManagementList[position]) {
                binding.txtEmpName.text = this.empName
                binding.empLvDate.text = this.leaveDate
                binding.empLvStatus.text = this.leaveStatus
            }
        }
    }

    override fun getItemCount(): Int {
        return leavesManagementList.size
    }

}