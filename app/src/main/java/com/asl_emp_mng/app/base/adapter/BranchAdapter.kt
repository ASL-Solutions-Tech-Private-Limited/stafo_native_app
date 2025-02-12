package com.asl_emp_mng.app.base.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyBranchItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.BranchItem

class BranchAdapter (
    private var list: List<BranchItem>,
    var context: Activity
) : RecyclerView.Adapter<BranchAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyBranchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyBranchItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtBranchName.text = this.branch_name
                binding.txtBranchAddress.text = this.branch_address
                binding.txtBranchRadius.text = this.radar.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}