package com.stafo.app.screens.tms

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTaskDescriptionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDescriptionBinding
    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupCommentList()
        setupAddCommentButton()
        loadDemoComments()
    }

    private fun setupData() {
        binding?.apply {
            taskStatus.text = "Overdue"
            taskPriority.text = "High Priority"
            taskPriority.setTextColor(
                ContextCompat.getColor(
                    this@TaskDescriptionActivity,
                    R.color.priority_high
                )
            )
            taskStatus.backgroundTintList =
                ContextCompat.getColorStateList(
                    this@TaskDescriptionActivity,
                    R.color.status_overdue
                )
        }
    }

    private fun setupCommentList() {
        commentAdapter = CommentAdapter(comments)
        binding.rvCommentList.apply {
            layoutManager = LinearLayoutManager(this@TaskDescriptionActivity)
            adapter = commentAdapter
        }
    }

    private fun setupAddCommentButton() {
        binding.btnAddComment.setOnClickListener {
            showAddCommentBottomSheet()
        }
    }

    private fun showAddCommentBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_comment, null)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitComment)

        btnSubmit.setOnClickListener {
            val text = etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                val newComment = Comment(
                    text = text, timestamp = getCurrentTime(), isMine = true
                )
                comments.add(newComment)
                commentAdapter.notifyItemInserted(comments.size - 1)
                binding.rvCommentList.scrollToPosition(comments.size - 1)
                bottomSheet.dismiss()
            }
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun loadDemoComments() {
        comments.addAll(
            listOf(
                Comment("Hey, did you finish this task?", "09:45 AM", isMine = false),
                Comment("Working on it. Will update by noon.", "09:47 AM", isMine = true),
                Comment("Okay, thanks!", "09:50 AM", isMine = false)
            )
        )
        commentAdapter.notifyDataSetChanged()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }


    data class Comment(
        val text: String, val timestamp: String, val isMine: Boolean
    )


}