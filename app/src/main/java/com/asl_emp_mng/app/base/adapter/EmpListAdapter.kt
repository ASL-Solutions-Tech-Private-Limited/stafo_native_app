package com.asl_emp_mng.app.base.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.ItemLeaveListBinding
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDataList

class EmpListAdapter(
    private var list: List<EmployeeDataList>,
    var context: Activity
) : RecyclerView.Adapter<EmpListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemLeaveListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaveListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvName.text = this.name
                binding.tvEmpId.text = this.emp_id
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}