package com.stafo.app.screens.tms

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTaskDescriptionBinding
import com.stafo.app.screens.tms.dataClass.TaskData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
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

            val task = intent.getSerializableExtra("task_data") as? TaskData
            if (task != null) {
                taskTitle.text = task.title
                taskDescription.text = task.description
                taskStatus.text = task.status
                taskPriority.text = task.priority

                when (task.priority.lowercase()) {
                    "low" -> {
                        taskPriority.setTextColor(ContextCompat.getColor(this@TaskDescriptionActivity, R.color.priority_low))
                    }
                    "medium" -> {
                        taskPriority.setTextColor(ContextCompat.getColor(this@TaskDescriptionActivity, R.color.priority_medium))
                    }
                    "high" -> {
                        taskPriority.setTextColor(ContextCompat.getColor(this@TaskDescriptionActivity, R.color.priority_high))
                    }
                    "urgent" -> {
                        taskPriority.setTextColor(ContextCompat.getColor(this@TaskDescriptionActivity, R.color.priority_urgent))
                    }
                }


                val statusLower = task.status.lowercase(Locale.getDefault())
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.parse(dateFormat.format(Date()))
                val taskEndDate = try {
                    dateFormat.parse(task.end_date)
                } catch (e: Exception) {
                    null
                }

                val isOverdue = taskEndDate != null && today != null && taskEndDate.before(today) && statusLower != "completed"

                when {
                    isOverdue -> {
                        taskStatus.text = "Overdue"
                        taskStatus.backgroundTintList =
                            ContextCompat.getColorStateList(this@TaskDescriptionActivity, R.color.status_overdue)
                    }
                    statusLower == "pending" -> {
                        taskStatus.backgroundTintList =
                            ContextCompat.getColorStateList(this@TaskDescriptionActivity, R.color.status_pending)
                    }
                    statusLower == "in progress" -> {
                        taskStatus.backgroundTintList =
                            ContextCompat.getColorStateList(this@TaskDescriptionActivity, R.color.status_in_progress)
                    }
                    statusLower == "completed" -> {
                        taskStatus.backgroundTintList =
                            ContextCompat.getColorStateList(this@TaskDescriptionActivity, R.color.status_completed)
                    }
                    else -> {
                        taskStatus.backgroundTintList =
                            ContextCompat.getColorStateList(this@TaskDescriptionActivity, R.color.grey_300)
                    }
                }

            }


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