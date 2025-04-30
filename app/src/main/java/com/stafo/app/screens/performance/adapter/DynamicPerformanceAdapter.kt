package com.stafo.app.screens.performance.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.performance.dataClass.PerformanceInput
import com.stafo.app.screens.performance.dataClass.DynamicPerformanceList

class DynamicPerformanceAdapter(
    private val inputList: MutableList<PerformanceInput>
) : RecyclerView.Adapter<DynamicPerformanceAdapter.DynamicViewHolder>() {

    inner class DynamicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.findViewById(R.id.tv_title)
        val editText: AppCompatEditText = view.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dynamic_performance_type_layout, parent, false)
        return DynamicViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val item = inputList[position]
        holder.textView.text = item.performanceType.name
        holder.editText.setText(item.amount)

        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.amount = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = inputList.size

    fun getUserInput(): List<DynamicPerformanceList> {
        return inputList.map {
            DynamicPerformanceList(
                type_id = it.performanceType.id,
                points = it.amount
            )
        }
    }
}
