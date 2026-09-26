package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceRequestListResponse(
    val status: Boolean,
    val message: String,
    val data: List<AttendanceRequestData>
)

data class AttendanceRequestData(
    val id: Int,
    @SerializedName("company_id") val company_id: Int,
    @SerializedName("branch_id") val branch_id: Int?,
    @SerializedName("department_id") val department_id: Int?,
    @SerializedName("employee_id") val employee_id: Int,
    val attendance: String?,
    val halfday: Int?,
    val date: String?,
    @SerializedName("in_time") val in_time: String?,
    @SerializedName("out_time") val out_time: String?,
    val reason: String?,
    @SerializedName("reject_reason") val reject_reason: String?,
    val status: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?,
    val employee: EmployeeRequest,
    val company: CompanyRequest?,
    val branch: BranchRequest?,
    val department: DepartmentRequest?
)

data class EmployeeRequest(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("emp_id") val emp_id: String? = null
)

data class CompanyRequest(
    val id: Int,
    @SerializedName("company_name") val company_name: String
)

data class BranchRequest(
    val id: Int,
    @SerializedName("branch_name") val branch_name: String
)

data class DepartmentRequest(
    val id: Int,
    val name: String
)

