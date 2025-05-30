package com.stafo.app.screens.tms.dataClass

import java.io.Serializable

data class TaskListResponse(
    val success: Boolean,
    val message: String,
    val data: List<TaskData>
)

data class TaskData(
    val id: Int,
    val company_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: String,
    val priority: String,
    val created_at: String,
    val updated_at: String,
    val company: Company,
    val assigned_employees: List<AssignedEmployee>
): Serializable

data class Company(
    val id: Int,
    val company_name: String,
    val company_code: String?,
    val email: String,
    val mobile_no: String
): Serializable

data class AssignedEmployee(
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val pivot: Pivot
): Serializable

data class Pivot(
    val task_id: Int,
    val employee_id: Int
): Serializable

