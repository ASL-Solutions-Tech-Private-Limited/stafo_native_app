package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyHolidayItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.Holiday
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterHoliday (
    private var list: List<Holiday>,
    var context: Context
) : RecyclerView.Adapter<AdapterHoliday.ViewHolder>() {
    inner class ViewHolder(val binding: RecyHolidayItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyHolidayItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         with(holder) {
             with(list[position]) {
                 binding.txtHolidayName.text = this.title
                 binding.txtStartDate.text = getFormatDate(this.start_date)
                 binding.txtEndDate.text = getFormatDate(this.end_date)
             }
         }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun getFormatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MMM/yy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}