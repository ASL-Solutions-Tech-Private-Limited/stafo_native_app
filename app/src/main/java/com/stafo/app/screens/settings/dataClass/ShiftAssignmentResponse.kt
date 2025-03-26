package com.stafo.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class ShiftAssignmentResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

/*data class ShiftAssignmentData(
    @SerializedName("employee") val employee: EmployeeShift
)

data class EmployeeShift(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val empId: String,
    @SerializedName("name") val name: String,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("marital_status") val maritalStatus: String?,
    @SerializedName("blood_group") val bloodGroup: String?,
    @SerializedName("guardian_name") val guardianName: String?,
    @SerializedName("country") val country: Any?,
    @SerializedName("state") val state: Any?,
    @SerializedName("city") val city: Any?,
    @SerializedName("address") val address: String?,
    @SerializedName("pin") val pin: Any?,
    @SerializedName("date_of_joining") val dateOfJoining: String?,
    @SerializedName("date_of_leaving") val dateOfLeaving: String?,
    @SerializedName("job_title_id") val jobTitleId: Any?,
    @SerializedName("employee_type_id") val employeeTypeId: Any?,
    @SerializedName("official_email_id") val officialEmailId: Any?,
    @SerializedName("esi_number") val esiNumber: Any?,
    @SerializedName("pf_number") val pfNumber: Any?,
    @SerializedName("privileged_leave") val privilegedLeave: Any?,
    @SerializedName("sick_leave") val sickLeave: Any?,
    @SerializedName("casual_leave") val casualLeave: Any?,
    @SerializedName("email") val email: String,
    @SerializedName("image") val image: Any?,
    @SerializedName("phone") val phone: String,
    @SerializedName("position") val position: String?,
    @SerializedName("salary") val salary: String?,
    @SerializedName("geo_status") val geoStatus: Any?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("shift_id") val shiftId: Int,
    @SerializedName("shift") val shift: Shift
)

data class Shift(
    @SerializedName("id") val id: Int,
    @SerializedName("shift_name") val shiftName: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)*/
