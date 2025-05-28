package com.stafo.app.screens.crm.dataClass

data class TaskListResponse(
    val success: Boolean,
    val message: String,
    val data: List<TaskData>
)

data class TaskData(
    val id: Int,
    val company_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: String,
    val priority: String,
    val created_at: String,
    val updated_at: String,
    val company: TaskListCompany
)

data class TaskListCompany(
    val id: Int,
    val company_name: String,
    val company_code: String?,
    val email: String,
    val mobile_no: String
)
