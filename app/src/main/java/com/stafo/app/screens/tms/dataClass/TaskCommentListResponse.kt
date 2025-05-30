package com.stafo.app.screens.tms.dataClass

data class TaskCommentListResponse(
    val success: Boolean,
    val message: String,
    val data: List<TaskComment>
)

data class TaskComment(
    val id: Int,
    val task_id: Int,
    val employee_id: Int,
    val company_id: Int,
    val comments: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val employee: Employee
)

data class Employee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String
)
