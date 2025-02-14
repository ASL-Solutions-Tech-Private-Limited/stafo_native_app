package com.asl_emp_mng.app.screens.settings.dataClass

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
    val position: String? = null,
    val salary: String? = null,
    val company_id: Int,
    val branch_name: String,
    val department_name: String,
    val last_punch_in: String? = null,
    val last_punch_out: String? = null,
    val attendances: List<Attendance> = emptyList()
)

data class Attendance(
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String,
    val out_time: String
)
