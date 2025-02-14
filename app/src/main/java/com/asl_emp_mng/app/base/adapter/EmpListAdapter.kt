package com.asl_emp_mng.app.base.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyViewEmployeeItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.ViewAllEmployeeActivity
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataList
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployee

class EmpListAdapter(
    private var list: List<GetEmployee>,
    var context: Activity
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

                binding.llcAddAttendance.setOnClickListener {
                    (context as ViewAllEmployeeActivity).showCustomBottomSheet()
                }

                binding.llcShiftTime.setOnClickListener {
                    (context as ViewAllEmployeeActivity).showShiftCustomBottomSheet()
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}