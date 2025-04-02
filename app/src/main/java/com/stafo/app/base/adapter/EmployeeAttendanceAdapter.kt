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
import com.stafo.app.screens.emp.EmployeeAttendance
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.settings.dataClass.EmployeeDataList
import com.stafo.app.utils.generateTextBitmap
import com.stafo.app.utils.showFullScreenImage

class EmployeeAttendanceAdapter(
    private var attendList: List<EmployeeDataList>,
    var context: Activity
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

                val placeholderBitmap = generateTextBitmap(this.name ?: "?")

                if (!this.attendances[position].punchOutImage.isNullOrEmpty()) {
                    binding.civEmp.visibility = View.VISIBLE

                    //val imageUrl = "${this.punchOutImage}/${this.selfieImage}".replace("\\", "")
                    val imageUrl = "${this.attendances[position].punchOutImage}"

                    Log.d("res","url image  punchOutImage ${this.attendances[position].punchOutImage}")

                    Glide.with(context)
                        .load(imageUrl)
                        .error(placeholderBitmap)
                        .into(binding.civEmp)

                    binding.civEmp.setOnClickListener {
                        showFullScreenImage(context,imageUrl)
                    }

                } else if (!this.attendances[position].punchInImage.isNullOrEmpty()){
                    binding.civEmp.visibility = View.VISIBLE

                    val imageUrl = "${this.attendances[position].punchInImage}"

                    Log.d("res","url image punchInImage ${this.attendances[position].punchInImage}")

                    Glide.with(context)
                        .load(imageUrl)
                        .error(placeholderBitmap)
                        .into(binding.civEmp)

                    binding.civEmp.setOnClickListener {
                        showFullScreenImage(context,imageUrl)
                    }
                }

                else {
                    binding.civEmp.setImageBitmap(placeholderBitmap)
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