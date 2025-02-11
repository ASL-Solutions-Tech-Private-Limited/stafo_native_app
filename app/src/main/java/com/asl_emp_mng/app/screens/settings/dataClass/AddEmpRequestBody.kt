package com.asl_emp_mng.app.screens.settings.dataClass

data class AddEmpRequestBody(
    val name: String,
    val email: String,
    val position: String,
    val salary: Int,
    val phone: String,
    val branch_id: Int,
    val department_id: Int
)
