package com.asl_emp_mng.app.screens.settings.dataClass

data class EmployeeListResponse(
    val status: Boolean,
    val message: String,
    val data: List<EmployeeDataList>
)

data class EmployeeDataList(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String?,
    val salary: String,
    val company_id: Int,
    val branch_name: String?,
    val department_name: String
)
