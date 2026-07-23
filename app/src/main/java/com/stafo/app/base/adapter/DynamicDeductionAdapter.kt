package com.stafo.app.base.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.settings.dataClass.SalaryComponent


class DynamicDeductionAdapter(
    private val fields: MutableList<SalaryComponent>, private val onAmountChanged: () -> Unit
) : RecyclerView.Adapter<DynamicDeductionAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.findViewById(R.id.tv_title)
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recy_dynamic_salary_item_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]

        holder.textView.text = if (field.amount_type == "Percentage" && field.percentage != null) {
            "${field.label} (${field.percentage}%)"
        } else {
            field.label
        }

        holder.editText.setText(field.amount?.toString() ?: "")

        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val newValue = s.toString().toDoubleOrNull() ?: 0.0
                    fields[pos].amount = newValue  // ✅ Fix: assign Double directly
                    onAmountChanged()
                }
            }
        })

    }

    override fun getItemCount(): Int = fields.size
}

