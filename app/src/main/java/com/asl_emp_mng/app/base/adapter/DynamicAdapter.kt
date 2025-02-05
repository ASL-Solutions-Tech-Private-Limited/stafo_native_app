package com.asl_emp_mng.app.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.DynamicField


class DynamicAdapter(private val fields: List<DynamicField>) :
    RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder>() {
    private val viewHolders = mutableListOf<DynamicViewHolder>()

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spinner: Spinner = view.findViewById(R.id.spinner_type)
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recy_dynamic_child_layout, parent, false)
        val viewHolder = DynamicViewHolder(view)
        viewHolders.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]

        holder.editText.hint = field.hint

        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_dropdown_item,
            field.options
        )
        holder.spinner.adapter = adapter
    }

    override fun getItemCount(): Int = fields.size

    fun isValid(): Boolean {
        var isValid = true

        for (viewHolder in viewHolders) {
            val editTextValue = viewHolder.editText.text.toString().trim()
            val spinnerSelectedItem = viewHolder.spinner.selectedItem?.toString()

            // Check if EditText is empty
            if (editTextValue.isEmpty()) {
                viewHolder.editText.error = "This field cannot be empty"
                isValid = false
            }

            // Check if Spinner has a valid selection
            if (spinnerSelectedItem.isNullOrEmpty()) {
                Toast.makeText(
                    viewHolder.itemView.context,
                    "Please select an option",
                    Toast.LENGTH_SHORT
                ).show()
                isValid = false
            }
        }
        return isValid
    }

}

