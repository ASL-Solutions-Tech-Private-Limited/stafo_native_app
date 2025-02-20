package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ItemEmpAttendaceLayoutBinding
import com.asl_emp_mng.app.screens.emp.EmployeeAttendanceRecordActivity
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataList
import com.asl_emp_mng.app.screens.ui.AutoSearchPlaceActivity
import com.asl_emp_mng.app.utils.getEmployeeDetails

class EmployeeAttendanceAdapter(
    private var attendList: List<EmployeeDataList>,
    var context: Context
) : RecyclerView.Adapter<EmployeeAttendanceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEmpAttendaceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmpAttendaceLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(attendList[position]) {
                binding.tvEmpName.text = this.name
                binding.tvEmpJobTitle.text = this.position
                if (this.attendances[0].attendance=="Absent") {

                    binding.tvCheckIn.text = this.attendances[0].attendance
                    binding.tvCheckIn.setTextColor(context.resources.getColor(R.color.reject))
                    binding.tvCheckOut.text = ""

                } else {
                    binding.tvCheckIn.text = this.attendances[0].in_time
                    binding.tvCheckOut.text = this.attendances[0].out_time
                }

                holder.itemView.setOnClickListener {
                     val employeeId=this.id
                    Log.d("res","emp :$employeeId")


                    val intent = Intent(context, AutoSearchPlaceActivity::class.java).apply {
                        putExtra("EMP_ID", employeeId.toString())
                    }
                    Log.d("res", "Sending EMP_ID: ${intent.getStringExtra("EMP_ID")}")
                    context.startActivity(intent)


                   /* context.startActivity(
                        Intent(
                            context,
                            AutoSearchPlaceActivity::class.java
                        ).apply {
                            putExtra("EMP_ID", employeeId)
                        }
                    )*/


                   // context.startActivity(Intent(context,AutoSearchPlaceActivity::class.java))
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return attendList.size
    }

}