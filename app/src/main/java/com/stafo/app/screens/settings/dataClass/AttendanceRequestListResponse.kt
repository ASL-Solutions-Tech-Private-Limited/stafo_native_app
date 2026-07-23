package com.stafo.app.screens.settings.dataClass

data class AttendanceRequestListResponse(
    val status: Boolean, val message: String, val data: List<AttendanceRequestData>
)

data class AttendanceRequestData(
    val id: Int,
    val company_id: Int,
    val branch_id: Int,
    val department_id: Int,
    val employee_id: Int,
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String,
    val out_time: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val employee: EmployeeRequest,
    val company: CompanyRequest,
    val branch: BranchRequest?,
    val department: DepartmentRequest?
)

data class EmployeeRequest(
    val id: Int, val name: String, val email: String, val phone: String
)

data class CompanyRequest(
    val id: Int, val company_name: String
)

data class BranchRequest(
    val id: Int, val branch_name: String
)

data class DepartmentRequest(
    val id: Int, val name: String
)

