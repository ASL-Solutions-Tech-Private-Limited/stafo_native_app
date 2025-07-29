package com.stafo.app.screens.settings.dataClass

data class AttendanceUpdateRequest(
    val employee_id: Int,
    val company_id: Int=0,
    val branch_id: Int=0,
    val department_id: Int=0,
    val attendance: String,
    val date: String,
    val in_time: String? = null,
    val out_time: String? = null,
    val halfday: Int? = null
)
