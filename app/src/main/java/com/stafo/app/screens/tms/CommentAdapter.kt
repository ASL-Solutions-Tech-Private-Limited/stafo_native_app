package com.stafo.app.screens.tms


import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemCommentBinding

class CommentAdapter(private val comments: List<TaskDescriptionActivity.Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        with(holder.binding) {
            tvComment.text = comment.text
            tvTime.text = comment.timestamp

            val params = messageLayout.layoutParams as LinearLayout.LayoutParams

            if (comment.isMine) {
                // Align to end (right)
                params.gravity = Gravity.END
                messageLayout.setBackgroundResource(R.drawable.bg_bubble_mine)
                tvComment.setTextColor(Color.WHITE)
            } else {
                // Align to start (left)
                params.gravity = Gravity.START
                messageLayout.setBackgroundResource(R.drawable.bg_bubble_other)
                tvComment.setTextColor(Color.BLACK)
            }

            messageLayout.layoutParams = params
        }
    }

    override fun getItemCount(): Int = comments.size
}

