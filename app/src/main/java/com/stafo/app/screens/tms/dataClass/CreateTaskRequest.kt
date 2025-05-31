package com.stafo.app.screens.tms.dataClass

data class CreateTaskRequest(
    val company_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: String,
    val priority: String,
    val task_assign: List<Int>,
    val files: List<String?>
)
