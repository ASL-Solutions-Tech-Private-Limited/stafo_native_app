package com.asl_emp_mng.app.screens.settings.dataClass


data class DepartmentResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<DataDepartment>
)

data class DataDepartment(
    val id: Int,
    val name: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

