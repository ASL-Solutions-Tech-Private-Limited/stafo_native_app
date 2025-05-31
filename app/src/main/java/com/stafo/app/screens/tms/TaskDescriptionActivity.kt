package com.stafo.app.screens.tms

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTaskDescriptionBinding
import com.stafo.app.screens.tms.adapter.AdapterAssignEmployee
import com.stafo.app.screens.tms.dataClass.AddCommentRequest
import com.stafo.app.screens.tms.dataClass.TaskData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import com.stafo.app.utils.showFormatDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDescriptionBinding

    private val tmsViewModel: TMSViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }


    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>()

    private var taskId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAddCommentButton()
        observeViewModel()
    }

    private fun setupData() {
        binding.apply {

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            val task = intent.getSerializableExtra("task_data") as? TaskData
            if (task != null) {
                taskId = task.id.toString()
                tmsViewModel.getCommentList(this@TaskDescriptionActivity, task.id)

                taskTitle.text = task.title
                taskDescription.text = task.description
                taskStatus.text = task.status
                taskPriority.text = task.priority
                taskDueDate.text = showFormatDate(task.endDate)

                val adapterAssign = AdapterAssignEmployee(task.assignedEmployees)
                rvAssignEmployee.apply {
                    layoutManager = LinearLayoutManager(this@TaskDescriptionActivity)
                    adapter = adapterAssign
                }

                when (task.priority.lowercase()) {
                    "low" -> {
                        taskPriority.setTextColor(
                            ContextCompat.getColor(
                                this@TaskDescriptionActivity, R.color.priority_low
                            )
                        )
                    }

                    "medium" -> {
                        taskPriority.setTextColor(
                            ContextCompat.getColor(
                                this@TaskDescriptionActivity, R.color.priority_medium
                            )
                        )
                    }

                    "high" -> {
                        taskPriority.setTextColor(
                            ContextCompat.getColor(
                                this@TaskDescriptionActivity, R.color.priority_high
                            )
                        )
                    }

                    "urgent" -> {
                        taskPriority.setTextColor(
                            ContextCompat.getColor(
                                this@TaskDescriptionActivity, R.color.priority_urgent
                            )
                        )
                    }
                }


                val statusLower = task.status.lowercase(Locale.getDefault())
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.parse(dateFormat.format(Date()))
                val taskEndDate = try {
                    dateFormat.parse(task.endDate)
                } catch (e: Exception) {
                    null
                }

                val isOverdue =
                    taskEndDate != null && today != null && taskEndDate.before(today) && statusLower != "completed"

                when {
                    isOverdue -> {
                        taskStatus.text = "Overdue"
                        taskStatus.backgroundTintList = ContextCompat.getColorStateList(
                            this@TaskDescriptionActivity, R.color.status_overdue
                        )
                    }

                    statusLower == "pending" -> {
                        taskStatus.backgroundTintList = ContextCompat.getColorStateList(
                            this@TaskDescriptionActivity, R.color.status_pending
                        )
                    }

                    statusLower == "in progress" -> {
                        taskStatus.backgroundTintList = ContextCompat.getColorStateList(
                            this@TaskDescriptionActivity, R.color.status_in_progress
                        )
                    }

                    statusLower == "completed" -> {
                        taskStatus.backgroundTintList = ContextCompat.getColorStateList(
                            this@TaskDescriptionActivity, R.color.status_completed
                        )
                    }

                    else -> {
                        taskStatus.backgroundTintList = ContextCompat.getColorStateList(
                            this@TaskDescriptionActivity, R.color.grey_300
                        )
                    }
                }

            }


        }
    }

    private fun observeViewModel() {


        tmsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        tmsViewModel.mTaskCommentListResponse.observe(this) {
            if (it.success) {
                if (!it.data.isNullOrEmpty()) {

                    commentAdapter = CommentAdapter(
                        this, it.data, onCommentLongPressed = { comment ->
                            AlertDialog.Builder(this).setTitle("Delete Comment")
                                .setMessage("Do you want to delete this comment?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //deleteCommentApi(comment.id)

                                    tmsViewModel.deleteComment(this, comment.id)
                                }.setNegativeButton("Cancel") { _, _ ->
                                    commentAdapter.clearSelection()
                                }.setOnDismissListener {
                                    commentAdapter.clearSelection()
                                }.show()
                        })
                    binding.rvCommentList.apply {
                        layoutManager = LinearLayoutManager(this@TaskDescriptionActivity)
                        adapter = commentAdapter
                    }


                }
            } else {
                CustomToast(this, it.message)
            }
        }
        tmsViewModel.mAddCommentResponse.observe(this) {
            if (it.message != null) {
                tmsViewModel.getCommentList(this, taskId.toInt())
            } else {
                CustomToast(this, it.message)
            }
        }
        tmsViewModel.mDeleteTaskResponse.observe(this) {
            if (it.status) {
                tmsViewModel.getCommentList(this@TaskDescriptionActivity, taskId.toInt())
                CustomToast(this, it.message)
            } else {
                CustomToast(this, it.message)
            }
        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    /*  private fun setupCommentList() {
          commentAdapter = CommentAdapter(comments)
          binding.rvCommentList.apply {
              layoutManager = LinearLayoutManager(this@TaskDescriptionActivity)
              adapter = commentAdapter
          }
      }*/

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
            val text = etComment.text.toString()
            if (text.isNotEmpty()) {
                val newComment = Comment(
                    text = text, timestamp = getCurrentTime(), isMine = true
                )

                if (getIsCOMPANYLogin(this)) {
                    val request = AddCommentRequest(
                        task_id = taskId.toInt(),
                        company_id = getEmployeeComId()?.toInt() ?: 0,
                        comments = text
                    )

                    tmsViewModel.addComment(this, request)
                } else {
                    val request = AddCommentRequest(
                        task_id = taskId.toInt(),
                        employee_id = getEmployeeDetails()?.id ?: 0,
                        comments = text
                    )

                    tmsViewModel.addComment(this, request)
                }


                /* comments.add(newComment)
                 commentAdapter.notifyItemInserted(comments.size - 1)
                 binding.rvCommentList.scrollToPosition(comments.size - 1)*/
                bottomSheet.dismiss()
            }
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    /*  private fun loadDemoComments() {
          comments.addAll(
              listOf(
                  Comment("Hey, did you finish this task?", "09:45 AM", isMine = false),
                  Comment("Working on it. Will update by noon.", "09:47 AM", isMine = true),
                  Comment("Okay, thanks!", "09:50 AM", isMine = false)
              )
          )
          commentAdapter.notifyDataSetChanged()
      }*/

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }


    data class Comment(
        val text: String, val timestamp: String, val isMine: Boolean
    )


}