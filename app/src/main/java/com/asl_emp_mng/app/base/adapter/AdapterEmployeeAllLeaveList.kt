package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.RecyEmpLeaveHistoryChildLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmpLeaveData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class AdapterEmployeeAllLeaveList (
    private var list: List<GetEmpLeaveData>,
    var context: Context
) : RecyclerView.Adapter<AdapterEmployeeAllLeaveList.ViewHolder>() {
    inner class ViewHolder(val binding: RecyEmpLeaveHistoryChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyEmpLeaveHistoryChildLayoutBinding.inflate(
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




                if (this.status=="approved"){
                    binding.txtStatus.setBackgroundResource(R.drawable.capsule_approve_button)
                }else if (this.status=="pending"){
                    binding.txtStatus.setBackgroundResource(R.drawable.capsule_pending_button)
                }else{
                    binding.txtStatus.setBackgroundResource(R.drawable.capsule_reject_button)
                }

                if (this.leaveType==1){
                    binding.txtLeaveType.text="Casual Leave"
                }else if (this.leaveType==2){
                    binding.txtLeaveType.text="Sick Leave"
                }else if (this.leaveType==3){
                    binding.txtLeaveType.text="Privillage Leave"
                }else{
                    binding.txtLeaveType.text="Casual Leave"
                }

                val capitalizedStatus = this.status.replaceFirstChar { it.uppercaseChar() }
                binding.txtStatus.text = capitalizedStatus
                binding.txtRqstDt.text = "${getFormatDate(this.fromDate)} - ${getFormatDate(this.toDate)}"


                binding.txtDays.text = "${calculateDuration(this.fromDate, this.toDate)} days"


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDuration(fromDate: String, toDate: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDateParsed = LocalDate.parse(fromDate.trim(), formatter)
            val toDateParsed = LocalDate.parse(toDate.trim(), formatter)
           // val daysBetween = ChronoUnit.DAYS.between(fromDateParsed, toDateParsed)

            val daysBetween = ChronoUnit.DAYS.between(fromDateParsed, toDateParsed) + 1
          /*  daysBetween.toString()
            if (daysBetween == 0L) {
                return "1 day"
            }*/

            "$daysBetween"

        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun getFormatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MMM/yy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

}