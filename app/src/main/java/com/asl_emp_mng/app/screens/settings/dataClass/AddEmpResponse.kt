package com.asl_emp_mng.app.screens.settings.dataClass

data class AddEmpResponse(
    val status: Boolean,
    val message: String,
    val data: EmployeeData
)

data class EmployeeData(
    val employee: Employee,
    val branch_name: String?,
    val department_name: String
)

data class Employee(
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val salary: Int,
    val company_id: Int,
    val branch_id: Int,
    val department_id: Int,
    val emp_id: String,
    val updated_at: String,
    val created_at: String,
    val id: Int,
    val branch: Branch,
    val department: Department
)

data class Branch(
    val id: Int,
    val company_id: Int,
    val branch_name: String,
    val branch_address: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

data class Department(
    val id: Int,
    val name: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

