package com.stafo.app.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.DynamicPolicyField

class DynamicPolicyAdapter(
    val fields: MutableList<DynamicPolicyField>,
    private val onFilePick: (Int) -> Unit
) :
    RecyclerView.Adapter<DynamicPolicyAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
        val editText2: AppCompatEditText = view.findViewById(R.id.editText2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recy_dynamic_policy_item_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val field = fields[position]

        holder.editText.setText(field.userInput)
        holder.editText.hint = field.hint

        holder.editText2.setText(field.userInput2)
        holder.editText2.hint = field.hint2

        holder.editText.addTextChangedListener {
            field.userInput = it.toString()
        }

        holder.editText2.addTextChangedListener {
            field.userInput2 = it.toString()
        }
        holder.editText2.setOnClickListener {
            onFilePick(position)
        }
    }



    override fun getItemCount(): Int = fields.size

    fun addField(newField: DynamicPolicyField) {
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

        }
        return isValid
    }
}