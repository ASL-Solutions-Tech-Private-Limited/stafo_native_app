package com.stafo.app.screens.settings.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.AttendanceRequestItemLayoutBinding
import com.stafo.app.screens.settings.AttendanceRequestActivity
import com.stafo.app.screens.settings.LeaveManagementActivity
import com.stafo.app.screens.settings.dataClass.AttendanceRequestData
import com.stafo.app.utils.generateTextBitmap
import com.stafo.app.utils.getFormatDate

class AdapterAttendanceRequest(
    private var list: List<AttendanceRequestData>, var context: Context,var userType:String
) : RecyclerView.Adapter<AdapterAttendanceRequest.ViewHolder>() {
    inner class ViewHolder(val binding: AttendanceRequestItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AttendanceRequestItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                val empNameDisplay = if (!this.employee.emp_id.isNullOrBlank()) {
                    "${this.employee.name} (${this.employee.emp_id})"
                } else {
                    this.employee.name
                }
                binding.txtEmpName.text = empNameDisplay
                binding.txtLeaveDate.text = getFormatDate(this.date)
                binding.txtStartDate.text = this.in_time ?: "--"
                binding.txtEndDate.text = this.out_time ?: "--"

                val placeholderBitmap = generateTextBitmap(this.employee.name ?: "?")
                binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)

                if (this.halfday == 0) {
                    binding.txtDuration.text = this.attendance ?: "Present (Full Day)"
                } else {
                    binding.txtDuration.text = this.attendance ?: "Half Day"
                }

                binding.txtBranch.text = this.branch?.branch_name ?: "--"
                binding.txtDepartment.text = this.department?.name ?: "--"

                binding.txtReason.text = if (!this.reason.isNullOrBlank()) this.reason else "--"

                if (!this.reject_reason.isNullOrBlank()) {
                    binding.txtRejectReason1.visibility = View.VISIBLE
                    binding.txtRejectReason.visibility = View.VISIBLE
                    binding.txtRejectReason.text = this.reject_reason
                } else {
                    binding.txtRejectReason1.visibility = View.GONE
                    binding.txtRejectReason.visibility = View.GONE
                }

                if (this.status == "Pending") {
                    binding.txtStatus.text = "Pending"
                    binding.txtStatus.setTextColor(context.getColor(R.color.pending_colour))
                    if (userType == "emp") {
                        binding.rtlActionRequest.visibility = View.GONE
                    } else {
                        binding.rtlActionRequest.visibility = View.VISIBLE
                    }
                } else if (this.status == "Approved") {
                    binding.txtStatus.text = "Approved"
                    binding.txtStatus.setTextColor(context.getColor(R.color.green))
                    binding.rtlActionRequest.visibility = View.GONE
                } else if (this.status == "Rejected") {
                    binding.txtStatus.text = "Rejected"
                    binding.txtStatus.setTextColor(context.getColor(R.color.pastel_red))
                    binding.rtlActionRequest.visibility = View.GONE
                }

                binding.btnApprove.setOnClickListener {
                    (context as AttendanceRequestActivity).actionRequest(this.id, "Approved")
                }
                binding.btnReject.setOnClickListener {
                    (context as AttendanceRequestActivity).openRejectDialog(this.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateList(newList: List<AttendanceRequestData>) {
        list = newList
        notifyDataSetChanged()
    }
}