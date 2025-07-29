package com.stafo.app.screens.emp.dataClass

data class AttendanceRequestResponse(
    val status: Boolean,
    val message: String,
    val data: AttendanceSubmitData
)
data class AttendanceSubmitData(
    val company_id: String,
    val branch_id: String,
    val employee_id: String,
    val attendance: String,
    val halfday: String,
    val date: String,
    val in_time: String,
    val out_time: String,
    val status: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
