package com.asl_emp_mng.app.base.adapter

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.DynamicHolidayField
import java.util.Calendar

class DynamicHolidayAdapter(private val fields: MutableList<DynamicHolidayField>) :
    RecyclerView.Adapter<DynamicHolidayAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
        val editText2: AppCompatEditText = view.findViewById(R.id.editText2)
        val editText3: AppCompatEditText = view.findViewById(R.id.editText3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recy_dynamic_holiday_item_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]
        holder.editText.setText(field.userInput)
        holder.editText.hint = field.hint
        holder.editText2.setText(field.userInput2)
        holder.editText2.hint = field.hint2
        holder.editText3.setText(field.userInput3)
        holder.editText3.hint = field.hint3



        holder.editText.addTextChangedListener {
            field.userInput = it.toString()
        }

        holder.editText2.addTextChangedListener {
            field.userInput2 = it.toString()
        }
        holder.editText3.addTextChangedListener {
            field.userInput3 = it.toString()
        }

        holder.editText2.setOnClickListener {
            showDatePicker(holder.editText2, field)
        }
        holder.editText3.setOnClickListener {
            showDatePicker(holder.editText3, field)
        }


    }



    private fun showDatePicker(editText: AppCompatEditText, field: DynamicHolidayField) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(editText.context, { _, selectedYear, selectedMonth, selectedDay ->
            //val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            val selectedDate = "$selectedYear/${selectedMonth + 1}/$$selectedDay"
            editText.setText(selectedDate)

            // Update the corresponding field value
            if (editText.id == R.id.editText2) {
                field.userInput2 = selectedDate
            } else if (editText.id == R.id.editText3) {
                field.userInput3 = selectedDate
            }

        }, year, month, day)

        datePickerDialog.show()
    }



    override fun getItemCount(): Int = fields.size

    fun getAllFields(): List<DynamicHolidayField> {
        return fields
    }

    fun addField(newField: DynamicHolidayField) {
        fields.add(newField)
        notifyItemInserted(fields.size - 1)
    }


    fun isValid(): Boolean {
        var isValid = true

        for (field in fields) {
            if (field.userInput.isBlank()) {
                isValid = false
                break
            }
            if (field.userInput2.isBlank()) {
                isValid = false
                break
            }
            if (field.userInput3.isBlank()) {
                isValid = false
                break
            }

        }

        return isValid
    }
}