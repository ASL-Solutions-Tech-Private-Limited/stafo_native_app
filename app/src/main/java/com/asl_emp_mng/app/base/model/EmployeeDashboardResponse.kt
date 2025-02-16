package com.asl_emp_mng.app.base.model

import com.asl_emp_mng.app.screens.settings.dataClass.Annyversary
import com.asl_emp_mng.app.screens.settings.dataClass.Birthday
import com.asl_emp_mng.app.screens.settings.dataClass.Leave
import com.google.gson.annotations.SerializedName

data class EmployeeDashboardResponse(
    val status: Boolean,
    val message: String,
    val employeeCount: Long,
    val presentCount: Long,
    val isLeaveToday: Boolean,
    val employeesOnLeave: List<Leave>,
    val birthday: List<Birthday>,
    val annyversary: List<Annyversary>,
    @SerializedName("employee_info")
    val employeeInfo: EmployeeInfo,
)

data class EmployeesOnLeave(
    val id: Long,
    @SerializedName("employee_id")
    val employeeId: Long,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    val reason: String,
    @SerializedName("leave_type")
    val leaveType: String?,
    @SerializedName("employee_basic_info")
    val employeeBasicInfo: EmployeeBasicInfo,
)

data class EmployeeBasicInfo(
    val id: Long,
    @SerializedName("emp_id")
    val empId: String,
    val name: String,
    val email: String,
    val phone: String,
    val image: Any?,
)

data class EmployeeInfo(
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
    val dateOfBirth: String,
    val gender: Any?,
    @SerializedName("marital_status")
    val maritalStatus: Any?,
    @SerializedName("blood_group")
    val bloodGroup: Any?,
    @SerializedName("guardian_name")
    val guardianName: Any?,
    val country: Any?,
    val state: Any?,
    val city: Any?,
    val address: Any?,
    val pin: Any?,
    @SerializedName("date_of_joining")
    val dateOfJoining: String,
    @SerializedName("date_of_leaving")
    val dateOfLeaving: Any?,
    @SerializedName("job_title_id")
    val jobTitleId: Any?,
    @SerializedName("employee_type_id")
    val employeeTypeId: Any?,
    @SerializedName("official_email_id")
    val officialEmailId: Any?,
    @SerializedName("esi_number")
    val esiNumber: Any?,
    @SerializedName("pf_number")
    val pfNumber: Any?,
    @SerializedName("privileged_leave")
    val privilegedLeave: Any?,
    @SerializedName("sick_leave")
    val sickLeave: Any?,
    @SerializedName("casual_leave")
    val casualLeave: Any?,
    val name: String,
    val email: String,
    val image: Any?,
    val phone: String,
    val position: String,
    val salary: String,
    @SerializedName("geo_status")
    val geoStatus: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val branch: Branch,
    val shift: Any?,
    val punches: List<Punch>?,
)

data class Branch(
    val id: Long,
    @SerializedName("company_id")
    val companyId: Long,
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("branch_address")
    val branchAddress: String,
    val latitude: String,
    val longitude: String,
    val radar: Long,
    val status: Long,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
)

data class Punch(
    val id: Long,
    @SerializedName("employee_id")
    val employeeId: Long,
    @SerializedName("punch_in")
    val punchIn: String?,
    @SerializedName("punch_out")
    val punchOut: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
)
