package com.stafo.app.screens.settings.dataClass

data class GetAllEmployeeResponse(
    val status: Boolean,
    val message: String,
    val data: List<GetEmployee>
)

data class GetEmployee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val salary: String?,
    val company_id: Int,
    val branch_name: String,
    val department_name: String,
    val geo_status: String?,
    val attendances: List<Attendance>
)

data class Attendance(
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String?,
    val out_time: String?
)
