package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.model.EmployeeAttendanceModel
import com.asl_emp_mng.app.databinding.RecyEmpAttendanceChildLayoutBinding

class EmployeeAttendanceAdapter(
    private var attendList: List<EmployeeAttendanceModel>,
    var context: Context
) : RecyclerView.Adapter<EmployeeAttendanceAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(attendList[position]) {

                if (this.attend){
                    binding.llcAttend.visibility= View.VISIBLE
                    binding.llcWeekOff.visibility= View.GONE
                }else{
                    binding.llcAttend.visibility= View.GONE
                    binding.llcWeekOff.visibility= View.VISIBLE
                }
               /* binding.tvTime.text = this.attendTime
                binding.tvDate.text = this.attendDate
                binding.tvCheckType.text = this.checkType*/
            }
        }
    }

    override fun getItemCount(): Int {
        return attendList.size
    }

}