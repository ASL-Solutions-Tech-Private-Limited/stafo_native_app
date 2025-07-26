package com.stafo.app.screens.settings.dataClass

data class AttendanceUpdateRequest(
    val employee_id: Int,
    val attendance: String,
    val date: String,
    val in_time: String? = null,
    val out_time: String? = null,
    val halfday: Int? = null
)
