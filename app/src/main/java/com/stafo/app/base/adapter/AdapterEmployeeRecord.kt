package com.stafo.app.base.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.DateItem
import com.stafo.app.databinding.RecyEmpAttendanceChildLayoutBinding
import com.stafo.app.screens.emp.EmpDayAttendanceRecordActivity
import com.stafo.app.utils.calculateHours2
import com.stafo.app.utils.extractDayNameDateAndMonth
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AdapterEmployeeRecord(
    private val context: Context,
    private val employeeId: String
) : ListAdapter<DateItem, AdapterEmployeeRecord.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: RecyEmpAttendanceChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyEmpAttendanceChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvDay.text = extractDayNameDateAndMonth(item.date).first
            tvDate.text = extractDayNameDateAndMonth(item.date).second.toString()

            if (item.isPresent == "Absent") {
                llcAttend.visibility = View.GONE
                llcWeekOff.visibility = View.VISIBLE
                tvWeekOffDay.text = extractDayNameDateAndMonth(item.date).first
                tvWeekOffDate.text = extractDayNameDateAndMonth(item.date).second.toString()
            } else {
                llcAttend.visibility = View.VISIBLE
                llcWeekOff.visibility = View.GONE
                tvCheckIn.text = item.punchIn ?: ""
                tvCheckOut.text = if (item.punchOut == "null") "" else item.punchOut ?: ""

                tvWorkingHrs.text =
                    if (!item.punchIn.isNullOrEmpty() && !item.punchOut.isNullOrEmpty()) {
                        calculateHours2(item.punchIn, item.punchOut)
                    } else {
                        ""
                    }
            }

            holder.itemView.setOnClickListener {

                val intent = Intent(context, EmpDayAttendanceRecordActivity::class.java).apply {
                    putExtra("EMP_ID", employeeId)
                }

                context.startActivity(intent)
            }

            val storeDate = item.date
            if (storeDate == getCurrentDate()) {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light_400))
            } else {
                holder.binding.clLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DateItem>() {
        override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
            return oldItem == newItem
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDatePosition(): Int {
        return currentList.indexOfFirst { it.date == getCurrentDate() }
    }
}


/*
class AdapterEmployeeRecord(
    private var list: List<DateItem>,
    var context: Context
) : RecyclerView.Adapter<AdapterEmployeeRecord.ViewHolder>() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvDay.text = extractDayNameDateAndMonth(this.date).first
                binding.tvDate.text = extractDayNameDateAndMonth(this.date).second.toString()
                if (this.isPresent == "Absent") {
                    binding.llcAttend.visibility = View.GONE
                    binding.llcWeekOff.visibility = View.VISIBLE
                    binding.tvWeekOffDay.text = extractDayNameDateAndMonth(this.date).first
                    binding.tvWeekOffDate.text = extractDayNameDateAndMonth(this.date).second.toString()

                } else if (this.isPresent == "Present") {
                    binding.llcAttend.visibility = View.VISIBLE
                    binding.llcWeekOff.visibility = View.GONE
                    binding.tvCheckIn.text = this.punchIn

                    if (this.punchOut=="null"){
                        binding.tvCheckOut.text =""
                    }else{
                        binding.tvCheckOut.text = this.punchOut
                    }



                    */
/*if (this.punchIn !=null && this.punchOut !=null){
                        binding.tvWorkingHrs.text = calculateHours(this.punchIn, this.punchOut)
                    }*//*


                    if (!this.punchIn.isNullOrEmpty() && !this.punchOut.isNullOrEmpty()) {
                        binding.tvWorkingHrs.text = calculateHours2(this.punchIn, this.punchOut)
                    } else {
                        binding.tvWorkingHrs.text = "--"
                    }


                } else {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }




}*/
