package com.stafo.app.screens.tms


import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemCommentBinding
import com.stafo.app.screens.tms.dataClass.TaskComment
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.getTimeOnly12HrFormat

class CommentAdapter(
    private val context: Context,
    private val comments: List<TaskComment>,
    private val onCommentLongPressed: (TaskComment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var selectedCommentId: Int? = null

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        with(holder.binding) {
            tvComment.text = comment.comments
            tvTime.text = getTimeOnly12HrFormat(comment.created_at)

            val params = messageLayout.layoutParams as LinearLayout.LayoutParams

            val isMine = if (getIsCOMPANYLogin(context)) {
                comment.company_id.toString() == getEmployeeComId()
            } else {
                comment.employee_id == getEmployeeDetails()?.id
            }

            if (isMine) {
                params.gravity = Gravity.END
                tvComment.setTextColor(Color.WHITE)
                tvTime.setTextColor(Color.WHITE)
            } else {
                params.gravity = Gravity.START
                tvComment.setTextColor(Color.BLACK)
                tvTime.setTextColor(Color.BLACK)
            }
            messageLayout.layoutParams = params

            if (comment.id == selectedCommentId) {
                messageLayout.setBackgroundResource(R.drawable.delete_bg_selected)
            } else {
                messageLayout.setBackgroundResource(
                    if (isMine) R.drawable.bg_bubble_mine else R.drawable.bg_bubble_other
                )
            }

            root.setOnLongClickListener {
                selectedCommentId = comment.id
                notifyDataSetChanged()
                onCommentLongPressed(comment)
                true
            }
        }
    }

    override fun getItemCount(): Int = comments.size

    fun clearSelection() {
        selectedCommentId = null
        notifyDataSetChanged()
    }
}



