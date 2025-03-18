package com.stafo.app.base.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.R
import com.stafo.app.databinding.ItemEmpAttendaceLayoutBinding
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.settings.dataClass.EmployeeDataList

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

                if (!this.image.isNullOrEmpty()) {
                    binding.civEmp.visibility = View.VISIBLE

                    val imageUrl = "${this.imagePath}/${this.image}".replace("\\", "")

                    Glide.with(context)
                        .load(imageUrl)
                        .into(binding.civEmp)

                } else {
                    binding.civEmp.visibility = View.GONE
                }



                if (this.attendances[0].attendance=="Absent") {

                    binding.tvCheckIn.text = this.attendances[0].attendance
                    binding.tvCheckIn.setTextColor(context.resources.getColor(R.color.reject))
                    binding.tvCheckOut.text = ""

                } else {
                    binding.tvCheckIn.text = this.attendances[0].in_time
                    binding.tvCheckOut.text = this.attendances[0].out_time
                }

                holder.itemView.setOnClickListener {
                     val employeeId=attendList[position].id


                    val intent = Intent(context, EmployeeAttendanceRecordActivity::class.java).apply {
                        putExtra("EMP_ID", employeeId.toString())
                    }

                    context.startActivity(intent)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return attendList.size
    }

    fun updateList(newList: List<EmployeeDataList>) {
        attendList = newList
        notifyDataSetChanged()
    }

}