package com.stafo.app.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.DynamicField


class DynamicAdapter(private val fields: MutableList<DynamicField>) :
    RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spinner: Spinner = view.findViewById(R.id.spinner_type)
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recy_dynamic_child_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]
        holder.editText.setText(field.userInput)
        holder.editText.hint = field.hint

        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_dropdown_item,
            field.options
        )
        holder.spinner.adapter = adapter
        val selectedIndex = field.options.indexOf(field.selectedOption)
        if (selectedIndex != -1) {
            holder.spinner.setSelection(selectedIndex)
        }

        holder.editText.addTextChangedListener {
            field.userInput = it.toString()
        }
        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                field.selectedOption = field.options[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = fields.size

    fun addField(newField: DynamicField) {
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

            if (field.selectedOption.isBlank()) {
                isValid = false
                break
            }
        }

        return isValid
    }
}



