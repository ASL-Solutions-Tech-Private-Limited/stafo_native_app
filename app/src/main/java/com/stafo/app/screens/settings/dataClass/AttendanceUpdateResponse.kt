package com.stafo.app.screens.settings.dataClass

data class AttendanceUpdateResponse(
    val status: Boolean,
    val message: String,
    val data: AttendanceData?
)

data class AttendanceData(
    val id: Int,
    val company_id: Int,
    val branch_id: String,
    val employee_id: String,
    val department_id: String,
    val attendance: String,
    val halfday: String,
    val date: String,
    val in_time: String,
    val out_time: String,
    val created_at: String,
    val updated_at: String,
    val punchin_image: String?,
    val punchout_image: String?
)
