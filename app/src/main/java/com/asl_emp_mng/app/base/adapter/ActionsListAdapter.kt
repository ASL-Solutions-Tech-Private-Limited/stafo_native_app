package com.asl_emp_mng.app.base.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.base.model.ActionModel
import com.asl_emp_mng.app.databinding.ItemActionLayoutBinding
import com.bumptech.glide.Glide

class ActionsListAdapter(
    private var list: List<ActionModel>,
    var context: Activity,
    var listener: ActionClickListener
) : RecyclerView.Adapter<ActionsListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemActionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActionLayoutBinding.inflate(
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
                Glide.with(context).load(icon).into(binding.ivAction)
                itemView.setOnClickListener { listener.onActionClick(name) }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface ActionClickListener {
        fun onActionClick(action: String)

    }
}