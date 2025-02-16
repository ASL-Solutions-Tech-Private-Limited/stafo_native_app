package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

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
    val id: Long,
    @SerializedName("emp_id")
    val empId: String,
    @SerializedName("company_id")
    val companyId: Long,
    @SerializedName("branch_id")
    val branchId: Long,
    @SerializedName("department_id")
    val departmentId: Long,
    @SerializedName("date_of_birth")
    val dateOfBirth: Any?,
    val gender: Any?,
    @SerializedName("marital_status")
    val maritalStatus: Any?,
    @SerializedName("blood_group")
    val bloodGroup: String?,
    @SerializedName("guardian_name")
    val guardianName: String?,
    val country: String?,
    val state: String?,
    val city: String?,
    val address: String?,
    val pin: String?,
    @SerializedName("date_of_joining")
    val dateOfJoining: String?,
    @SerializedName("date_of_leaving")
    val dateOfLeaving: String?,
    @SerializedName("job_title_id")
    val jobTitleId: String?,
    @SerializedName("employee_type_id")
    val employeeTypeId: String?,
    @SerializedName("official_email_id")
    val officialEmailId: String?,
    @SerializedName("esi_number")
    val esiNumber: String?,
    @SerializedName("pf_number")
    val pfNumber: String?,
    @SerializedName("privileged_leave")
    val privilegedLeave: String?,
    @SerializedName("sick_leave")
    val sickLeave: String?,
    @SerializedName("casual_leave")
    val casualLeave: String?,
    val name: String,
    val email: String,
    val image: String?,
    val phone: String,
    val position: String,
    val salary: String?,
    @SerializedName("geo_status")
    val geoStatus: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("shift_id")
    val shiftId: String?,

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

