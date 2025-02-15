package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class LeaveResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("leaveCount") val leaveCount: List<Any>,
    @SerializedName("data") val data: List<LeaveData>
)

data class LeaveData(
    @SerializedName("id") val id: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("leave_type") val leaveType: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("employee_basic_info") val employeeBasicInfo: LeaveEmployeeBasicInfo
)

data class LeaveEmployeeBasicInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val empId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String?,
    @SerializedName("privileged_leave") val privilegedLeave: Int?,
    @SerializedName("sick_leave") val sickLeave: Int?,
    @SerializedName("casual_leave") val casualLeave: Int?
)
