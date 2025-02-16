package com.asl_emp_mng.app.screens.settings.dataClass

import com.asl_emp_mng.app.base.model.EmployeeAttendanceModel

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
    val position: String? = null,
    val salary: String? = null,
    val company_id: Int,
    val branch_name: String,
    val department_name: String,
    val last_punch_in: String? = null,
    val last_punch_out: String? = null,
    val attendances: List<EmployeeAttendanceModel>
)




