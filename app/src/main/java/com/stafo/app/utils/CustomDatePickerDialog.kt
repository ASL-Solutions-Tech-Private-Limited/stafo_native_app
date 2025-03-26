package com.stafo.app.utils

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.core.view.children
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.N)
class CustomDatePickerDialog(context: Context, private val onDateSetListener: OnDateSetListener) :
    DatePickerDialog(context) {

    private lateinit var datePicker: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        datePicker = findDatePicker(this)!!

        // Customize the layout to include separate NumberPickers for day, month, and year
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL

        val dayPicker = NumberPicker(context)
        dayPicker.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        ll.addView(dayPicker)

        val monthPicker = NumberPicker(context)
        monthPicker.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.displayedValues = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        ll.addView(monthPicker)

        val yearPicker = NumberPicker(context)
        yearPicker.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        ll.addView(yearPicker)

        datePicker.addView(ll)

        // Set the date change listener to update the date in the title
        datePicker.init(
            yearPicker.value, monthPicker.value - 1, dayPicker.value,
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                onDateSetListener.onDateSet(
                    datePicker, year, monthOfYear + 1, dayOfMonth
                )
            }
        )
    }

    // Helper function to find the DatePicker inside DatePickerDialog
    private fun findDatePicker(dialog: DatePickerDialog): DatePicker? {
        for (child in dialog.datePicker.children) {
            if (child is DatePicker) {
                return child
            }
        }
        return null
    }
}

interface OnDateSetListener {
    fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
}