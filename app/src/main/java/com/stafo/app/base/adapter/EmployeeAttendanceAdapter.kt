package com.stafo.app.base.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stafo.app.R
import com.stafo.app.databinding.ItemEmpAttendaceLayoutBinding
import com.stafo.app.screens.emp.EditAttendanceActivity
import com.stafo.app.screens.emp.EmployeeAttendance
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.settings.dataClass.EmployeeDataList
import com.stafo.app.utils.convertTo12Hour
import com.stafo.app.utils.generateTextBitmap
import com.stafo.app.utils.showFullScreenImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeAttendanceAdapter(
    private var attendList: List<EmployeeDataList>, var context: Activity,var selectDate:String
) : RecyclerView.Adapter<EmployeeAttendanceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEmpAttendaceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmpAttendaceLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(attendList[position]) {
                binding.tvEmpName.text = this.name

                val attendance = this.attendances.getOrNull(0)
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                binding.ivUpdateAttendance.setOnClickListener {
                    val intent = Intent(context, EditAttendanceActivity::class.java).apply {
                        Log.e("attendance",this@with.id.toString())
                        putExtra("EMPID", attendance?.id.toString())
                        putExtra("mDate", selectDate)
                        putExtra("EMPNAME", this@with.name ?: "")
                    }
                    context.startActivity(intent)
                }


                val placeholderBitmap = generateTextBitmap(this.name ?: "?")

                if (this.attendances.isNotEmpty()) {
                    val firstAttendance = this.attendances[0]

                    if (!firstAttendance.punchOutImage.isNullOrBlank()) {
                        binding.civEmp.visibility = View.VISIBLE
                        val imageUrl = firstAttendance.punchOutImage
                        Log.d("res", "url image punchOutImage $imageUrl")
                        Glide.with(context).load(imageUrl).error(placeholderBitmap).into(binding.civEmp)

                        binding.civEmp.setOnClickListener {
                            showFullScreenImage(context, imageUrl)
                        }

                    } else if (!firstAttendance.punchInImage.isNullOrBlank()) {
                        binding.civEmp.visibility = View.VISIBLE
                        val imageUrl = firstAttendance.punchInImage
                        Log.d("res", "url image punchInImage $imageUrl")
                        Glide.with(context).load(imageUrl).error(placeholderBitmap).into(binding.civEmp)

                        binding.civEmp.setOnClickListener {
                            showFullScreenImage(context, imageUrl)
                        }

                    } else {
                        binding.civEmp.setImageBitmap(placeholderBitmap)
                    }

                    if (firstAttendance.attendance == "Absent") {
                        binding.tvCheckIn.text = firstAttendance.attendance
                        binding.tvCheckIn.setTextColor(context.resources.getColor(R.color.reject))
                        binding.tvCheckOut.text = ""
                        binding.ivUpdateAttendance.visibility = View.VISIBLE
                    } else {
                        val inTime = firstAttendance.in_time
                        val outTime = this.attendances.last().out_time

                        binding.tvCheckIn.text = convertTo12Hour(inTime)
                        binding.tvCheckOut.text = convertTo12Hour(outTime)

                        // Hide ivUpdateAttendance only if both in_time and out_time are not null/blank
                        if (!inTime.isNullOrBlank() && !outTime.isNullOrBlank()) {
                            binding.ivUpdateAttendance.visibility = View.GONE
                        } else {
                            if (selectDate == today) {
                                binding.ivUpdateAttendance.visibility = View.GONE
                            } else {
                                binding.ivUpdateAttendance.visibility = View.VISIBLE
                            }
                        }



                    }
                } else {


                    if (selectDate == today) {
                        binding.ivUpdateAttendance.visibility = View.GONE
                    }

                    binding.civEmp.setImageBitmap(placeholderBitmap)
                    binding.tvCheckIn.text = "Absent"
                    binding.tvCheckIn.setTextColor(context.resources.getColor(R.color.reject))
                    binding.tvCheckOut.text = ""
                }







                holder.itemView.setOnClickListener {
                    val employeeId = attendList[position].id
                    val intent =
                        Intent(context, EmployeeAttendanceRecordActivity::class.java).apply {
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