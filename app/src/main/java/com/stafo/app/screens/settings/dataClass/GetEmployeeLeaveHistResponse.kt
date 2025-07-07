package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class GetEmployeeLeaveHistResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("leaveCount") val leaveCount: List<LeaveCount>,
    @SerializedName("data") val data: List<GetEmpLeaveData>
)

data class LeaveCount(
    @SerializedName("leave_type") val leaveType: Int,
    @SerializedName("total_days") val totalDays: String,
   val leaveTypeName: String? = null,
)

data class GetEmpLeaveData(
    @SerializedName("id") val id: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("leave_type") val leaveType: Int?,
    @SerializedName("days") val days: Int,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("employee_basic_info") val employeeBasicInfo: GetEmployeeBasicInfo,
    @SerializedName("leavetype") val leaveTypeObj: LeaveType
)

data class GetEmployeeBasicInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val empId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String?,
    @SerializedName("privileged_leave") val privilegedLeave: Int,
    @SerializedName("sick_leave") val sickLeave: Int,
    @SerializedName("casual_leave") val casualLeave: Int
)
data class LeaveType(
    val id: Int,
    val name: String?,
    val no_of_days: Int?,
    val description: String?,
    val is_paid: Int
)