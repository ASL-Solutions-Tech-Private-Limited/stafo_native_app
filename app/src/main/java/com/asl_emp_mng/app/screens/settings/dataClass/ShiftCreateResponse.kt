package com.asl_emp_mng.app.screens.settings.dataClass

data class ShiftCreateResponse(
    val success: Boolean,
    val message: String,
    val data: ShiftData
)

data class ShiftData(
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
