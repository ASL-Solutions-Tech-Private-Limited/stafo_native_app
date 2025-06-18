package com.stafo.app.screens.tms.dataClass

data class CreateTaskResponse(
    val success: Boolean,
    val message: String,
    val data: CreatedTaskData
)

data class CreatedTaskData(
    val company_id: String,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: String,
    val priority: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)