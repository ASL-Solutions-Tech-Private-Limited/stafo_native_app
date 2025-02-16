package com.asl_emp_mng.app.base.model

import com.google.gson.annotations.SerializedName

data class MonthAttendaceResponse(
    val status: Boolean,
    val message: String,
    val data: ArrayList<AttendanceHistory>?
)


data class AttendanceHistory(
    val id: Long,
    @SerializedName("company_id")
    val companyId: Long,
    @SerializedName("branch_id")
    val branchId: Long,
    @SerializedName("employee_id")
    val employeeId: Long,
    @SerializedName("department_id")
    val departmentId: Any?,
    val attendance: String,
    val halfday: Long,
    val date: String,
    @SerializedName("in_time")
    val inTime: String,
    @SerializedName("out_time")
    val outTime: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val branch: Branch,
    val employee: Employee,
    val department: Any?,
)

