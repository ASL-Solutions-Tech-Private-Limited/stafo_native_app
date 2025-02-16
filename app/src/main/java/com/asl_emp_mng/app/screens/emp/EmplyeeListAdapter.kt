package com.asl_emp_mng.app.screens.emp

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.ItemLeaveListBinding


class EmplyeeListAdapter(
    private val context: Activity,
//        private val list: List<EmplyeeDetails>,
//        private val listener: (item: EmplyeeDetails) -> Unit
) : RecyclerView.Adapter<EmplyeeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLeaveListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 6
    }

    inner class ViewHolder(private val binding: ItemLeaveListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}