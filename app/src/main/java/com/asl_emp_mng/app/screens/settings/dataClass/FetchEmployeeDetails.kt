package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class FetchEmployeeDetails(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: EmployeeDataFetch?,
    @SerializedName("image_url") val imageUrl: String?
)

data class EmployeeDataFetch(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val empId: String,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("marital_status") val maritalStatus: String?,
    @SerializedName("blood_group") val bloodGroup: String?,
    @SerializedName("guardian_name") val guardianName: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("pin") val pin: String?,
    @SerializedName("date_of_joining") val dateOfJoining: String?,
    @SerializedName("date_of_leaving") val dateOfLeaving: String?,
    @SerializedName("job_title_id") val jobTitleId: Int?,
    @SerializedName("employee_type_id") val employeeTypeId: Int?,
    @SerializedName("official_email_id") val officialEmailId: String?,
    @SerializedName("esi_number") val esiNumber: String?,
    @SerializedName("pf_number") val pfNumber: String?,
    @SerializedName("privileged_leave") val privilegedLeave: Int?,
    @SerializedName("sick_leave") val sickLeave: Int?,
    @SerializedName("casual_leave") val casualLeave: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("image") val image: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("position") val position: String?,
    @SerializedName("salary") val salary: String?,
    @SerializedName("geo_status") val geoStatus: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("branch_name") val brancName: String?,
    @SerializedName("department_name") val departmentName: String?
)
