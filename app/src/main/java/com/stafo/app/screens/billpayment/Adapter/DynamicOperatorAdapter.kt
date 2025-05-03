package com.stafo.app.screens.billpayment.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.billpayment.dataClass.ParamInfo

class DynamicOperatorAdapter (
    private val inputList: List<ParamInfo>
) : RecyclerView.Adapter<DynamicOperatorAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.findViewById(R.id.tv_title)
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dynamic_operator_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val item = inputList[position]
        holder.textView.text = item.paramName
        holder.editText.hint=item.paramName

    }

    override fun getItemCount(): Int = inputList.size


}