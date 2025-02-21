package com.asl_emp_mng.app.base.model

import com.google.gson.annotations.SerializedName

/*data class MonthAttendaceResponse(
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
)*/

data class MonthAttendaceResponse(
    val status: Boolean,
    val message: String,
    val data: List<AttendanceHistory>
)

data class AttendanceHistory(
    val id: Int,
    val company_id: Int,
    val branch_id: Int,
    val employee_id: Int,
    val department_id: Int?,
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String?,
    val out_time: String?,
    val created_at: String,
    val updated_at: String,
    val branch: AttendBranch,
    val employee: AttendEmployee,
    val department: Any?
)

data class AttendBranch(
    val id: Int,
    val company_id: Int,
    val branch_name: String,
    val branch_address: String,
    val latitude: String,
    val longitude: String,
    val radar: Int,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

data class AttendEmployee(
    val id: Int,
    val emp_id: String,
    val company_id: Int,
    val branch_id: Int,
    val department_id: Int?,
    val date_of_birth: String,
    val gender: String,
    val marital_status: String,
    val blood_group: String,
    val guardian_name: String,
    val country: String,
    val state: String,
    val city: String,
    val address: String,
    val pin: String?,
    val date_of_joining: String,
    val date_of_leaving: String?,
    val job_title_id: Int,
    val employee_type_id: Int,
    val official_email_id: String?,
    val esi_number: String?,
    val pf_number: String?,
    val privileged_leave: String?,
    val sick_leave: String?,
    val casual_leave: String?,
    val name: String,
    val email: String,
    val image: String?,
    val phone: String,
    val position: String,
    val salary: String,
    val geo_status: String?,
    val created_at: String,
    val updated_at: String,
    val shift_id: Int?,
    val attendance_type: String?
)
