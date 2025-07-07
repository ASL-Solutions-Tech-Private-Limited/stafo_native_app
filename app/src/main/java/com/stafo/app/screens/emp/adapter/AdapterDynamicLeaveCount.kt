package com.stafo.app.screens.emp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.CustomLeaveHistoryLeaveTypeItemLayoutBinding
import com.stafo.app.screens.settings.dataClass.LeaveCount

class AdapterDynamicLeaveCount(
    private var list: List<LeaveCount>,
    var context: Activity,
) : RecyclerView.Adapter<AdapterDynamicLeaveCount.ViewHolder>() {
    inner class ViewHolder(val binding: CustomLeaveHistoryLeaveTypeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomLeaveHistoryLeaveTypeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvLeaveTypeName.text = formatLeaveType(this.leaveTypeName)
                binding.tvLvCount.text = this.totalDays?:""

            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    private fun formatLeaveType(name: String?): String {
        if (name.isNullOrBlank()) return ""

        val words = name.trim().split(" ")
        return if (words.size >= 2) {
            "${words[0]}\n${words.subList(1, words.size).joinToString(" ")}"
        } else {
            "${words[0]}\n"
        }
    }



}
