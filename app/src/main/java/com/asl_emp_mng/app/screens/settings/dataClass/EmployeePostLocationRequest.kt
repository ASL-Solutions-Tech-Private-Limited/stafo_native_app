package com.asl_emp_mng.app.screens.settings.dataClass

data class EmployeePostLocationRequest(
    val employee_id: String,
    val latitude: String,
    val longitude: String
)