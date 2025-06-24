package com.stafo.app.screens.expense.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.stafo.app.R
import com.stafo.app.screens.expense.dataClass.DynamicExpenseField

class DynamicExpenseAdapter (private val fields: MutableList<DynamicExpenseField>) :
    RecyclerView.Adapter<DynamicExpenseAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: TextInputEditText = view.findViewById(R.id.tie_expense_field)
        val editText2: TextInputEditText = view.findViewById(R.id.tie_expense_input_type)
        val removeItem: ShapeableImageView = view.findViewById(R.id.img_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_child_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]
        holder.editText.setText(field.userInput)
        holder.editText2.setText(field.inputType)




        holder.editText.addTextChangedListener {
            field.userInput = it.toString()
        }

        holder.editText2.addTextChangedListener {
            field.inputType = it.toString()
        }

        holder.removeItem.setOnClickListener {
            removeField(position)
        }




    }








    override fun getItemCount(): Int = fields.size

    fun getAllFields(): List<DynamicExpenseField> {
        return fields
    }

    fun removeField(position: Int) {
        if (position in fields.indices) {
            fields.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, fields.size)
        }
    }

    fun addField(newField: DynamicExpenseField) {
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
            if (field.inputType.isBlank()) {
                isValid = false
                break
            }
        }

        return isValid
    }
}