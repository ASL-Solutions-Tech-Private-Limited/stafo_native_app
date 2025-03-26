package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class PendingLeaveResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<LeaveRequest>
)

data class LeaveRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("employee_basic_info") val employeeBasicInfo: EmployeeBasicInfo
)

data class EmployeeBasicInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val empId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)
