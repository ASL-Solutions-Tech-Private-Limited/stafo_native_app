package com.stafo.app.screens.tms.dataClass

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TaskListResponse(
    val success: Boolean,
    val message: String,
    val data: List<TaskData>,
    @SerializedName("file_path") val filePath: String
)

data class TaskData(
    val id: Int,
    @SerializedName("company_id") val companyId: Int,
    val title: String,
    val description: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val status: String,
    val priority: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val company: Company,
    @SerializedName("assigned_employees") val assignedEmployees: List<AssignedEmployee>,
    @SerializedName("task_files") val taskFiles: List<TaskFile>
) : Serializable

data class Company(
    val id: Int,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_code") val companyCode: String?,
    val email: String,
    @SerializedName("mobile_no") val mobileNo: String
) : Serializable

data class AssignedEmployee(
    @SerializedName("emp_id") val empId: String,
    val name: String,
    val email: String,
    val phone: String,
    val pivot: Pivot
) : Serializable

data class Pivot(
    @SerializedName("task_id") val taskId: Int, @SerializedName("employee_id") val employeeId: Int
) : Serializable

data class TaskFile(
    @SerializedName("task_id") val taskId: Int, val filename: String
) : Serializable
