package com.stafo.app.screens.tms

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemTaskCardLayoutBinding
import com.stafo.app.screens.tms.dataClass.TaskData

class TaskAdapter(
    private val tasks: MutableList<TaskData>,
    private val onEditClick: (TaskData) -> Unit,
    private val onDeleteClick: (TaskData) -> Unit,
    private val itemClick: (TaskData) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ItemTaskCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val ctx = holder.itemView.context

        with(holder.binding) {
            taskTitle.text = task.title
            taskDate.text = "${task.startDate} ·"
            taskPriority.text = "${task.priority} Priority"
            taskStatus.text = task.status

            taskStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
            val tintColor = when (task.status) {
                "In Progress" -> ContextCompat.getColor(ctx, R.color.status_in_progress)
                "Pending" -> ContextCompat.getColor(ctx, R.color.status_pending)
                "Completed" -> ContextCompat.getColor(ctx, R.color.status_completed)
                "Overdue" -> ContextCompat.getColor(ctx, R.color.status_overdue)
                else -> ContextCompat.getColor(ctx, R.color.gray_colour)
            }

            ViewCompat.setBackgroundTintList(taskStatus, ColorStateList.valueOf(tintColor))


            val priorityColor = when (task.priority) {
                "High" -> ContextCompat.getColor(ctx, R.color.priority_high)
                "Medium" -> ContextCompat.getColor(ctx, R.color.priority_medium)
                "Low" -> ContextCompat.getColor(ctx, R.color.priority_low)
                else -> ContextCompat.getColor(ctx, R.color.gray_colour)
            }

            taskPriority.setTextColor(priorityColor)
            ivEdit.setOnClickListener { onEditClick(task) }
            ivDelete.setOnClickListener { onDeleteClick(task) }
            root.setOnClickListener {
                itemClick(task)
            }
        }
    }

    fun updateData(newTasks: List<TaskData>) {
        (tasks as? MutableList)?.clear()
        (tasks as? MutableList)?.addAll(newTasks)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tasks.size
}
