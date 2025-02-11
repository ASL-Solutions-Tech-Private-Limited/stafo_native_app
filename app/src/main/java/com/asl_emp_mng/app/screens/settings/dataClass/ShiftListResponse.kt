package com.asl_emp_mng.app.screens.settings.dataClass

data class ShiftListResponse(
    val success: Boolean,
    val message: String,
    val data: List<ShiftDataList>
)

data class ShiftDataList(
    val id: Int,
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val created_at: String,
    val updated_at: String
)
