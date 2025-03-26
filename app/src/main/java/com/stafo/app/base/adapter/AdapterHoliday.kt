package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RecyHolidayItemLayoutBinding
import com.stafo.app.screens.settings.HolidayActivity
import com.stafo.app.screens.settings.dataClass.Holiday
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

                 if (position % 2 == 0) {
                     binding.llDate.setBackgroundResource(R.drawable.holiday_item_bg2)
                 } else {
                     binding.llDate.setBackgroundResource(R.drawable.holiday_item_bg)
                 }

                 binding.txtHolidayName.text = this.title
                 binding.txtHolidayDay.text = formatDay(this.start_date)
                 binding.txtHolidayDate.text = formatDate(this.start_date)
                 binding.txtHolidayMonth.text = formatMonthDate(this.start_date)

                 binding.itemDelete.setOnClickListener {
                     showCompanyDeleteDialog(this.id)
                 }

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


    private fun formatMonthDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("MMM", Locale.ENGLISH)

            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: return dateStr)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("d", Locale.ENGLISH)

            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: return dateStr)
        } catch (e: Exception) {
            dateStr
        }
    }


    private fun formatDay(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)

            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: return dateStr)
        } catch (e: Exception) {
            dateStr
        }
    }


    private fun showCompanyDeleteDialog(itemId:Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            (context as HolidayActivity).deleteHoliday(itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}
