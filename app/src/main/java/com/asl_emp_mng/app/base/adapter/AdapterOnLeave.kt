package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.ItemActivityListBinding
import com.asl_emp_mng.app.databinding.ItemLeaveListBinding
import com.asl_emp_mng.app.screens.settings.dataClass.Leave
import com.asl_emp_mng.app.screens.settings.dataClass.OnLeaveResponse
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdapterOnLeave (
    private var list: List<Leave>,
    var context: Context
) : RecyclerView.Adapter<AdapterOnLeave.ViewHolder>() {
    inner class ViewHolder(val binding: ItemLeaveListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaveListBinding.inflate(
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

                binding.tvName.text = this.employee_basic_info.name

                var fromDate=getFormatDate(this.from_date)
                var toDate=getFormatDate(this.to_date)

                binding.tvLeaveDate.text ="$fromDate - $toDate"

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

  /*  @RequiresApi(Build.VERSION_CODES.O)
    private fun showDate(date:String):String{

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatDate = LocalDate.parse(date, inputFormatter).format(outputFormatter)
        return formatDate
    }*/
    private fun getFormatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MMM/yy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}