package com.asl_emp_mng.app.base.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyViewEmployeeItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataList
import com.asl_emp_mng.app.screens.ui.EmployeeAttendance

class EmpListAdapter(
    private var list: List<EmployeeDataList>,
    var context: Activity,
    var from: String
) : RecyclerView.Adapter<EmpListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyViewEmployeeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyViewEmployeeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.name
                binding.txtMobile.text = this.phone
                binding.txtEmail.text = this.email
                binding.txtJobTitle.text = this.position


                if (from == "View All") {
                    binding.llcViewProfile.visibility = View.VISIBLE
                    binding.llcReqLocation.visibility = View.VISIBLE
                    binding.llcAddAttendance.visibility = View.GONE
                    binding.llcShiftTime.visibility = View.GONE
                    binding.llcViewProfile.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context,
                                EmployeeAttendance::class.java
                            ).apply {
                                putExtra("EmpData", list[position].emp_id)
                            })
                    }
                } else {
                    binding.llcViewProfile.visibility = View.GONE
                    binding.llcReqLocation.visibility = View.GONE
                    binding.llcAddAttendance.visibility = View.VISIBLE
                    binding.llcShiftTime.visibility = View.VISIBLE
                    binding.llcViewProfile.setOnClickListener {

                    }
                }

                binding.llcAddAttendance.setOnClickListener {
                    (context as ViewAllEmployeeActivity).showCustomBottomSheet(this.id.toString())
                }

                binding.llcShiftTime.setOnClickListener {
                    (context as ViewAllEmployeeActivity).showShiftCustomBottomSheet(this.id.toString())
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onEMPClickListener {
        fun onEMPClick(empID: String)
    }


}