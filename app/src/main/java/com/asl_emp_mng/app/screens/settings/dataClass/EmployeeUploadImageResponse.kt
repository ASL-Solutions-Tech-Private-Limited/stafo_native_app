package com.asl_emp_mng.app.screens.settings.dataClass

data class EmployeeUploadImageResponse(
    val status: Boolean,
    val message: String,
    val data: ImageEmployeeData
)

data class ImageEmployeeData(
    val employee: ImageEmployee,
    val branch_name: String,
    val department_name: String,
    val image_url: String
)

data class ImageEmployee(
    val id: Int,
    val emp_id: String,
    val company_id: Int,
    val branch_id: Int,
    val department_id: Int,
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
    val date_of_leaving: String,
    val job_title_id: Int,
    val employee_type_id: Int,
    val official_email_id: String,
    val esi_number: String,
    val pf_number: String,
    val privileged_leave: String?,
    val sick_leave: String?,
    val casual_leave: String?,
    val name: String,
    val email: String,
    val image: String,
    val phone: String,
    val position: String,
    val salary: String,
    val geo_status: String,
    val created_at: String,
    val updated_at: String,
    val shift_id: Int,
    val attendance_type: String?,
    val aadhar: String?,
    val pan: String?,
    val voter: String?,
    val driving_license: String?,
    val uan: String?,
    val aadhar_verify: String,
    val pan_verify: String,
    val voter_verify: String,
    val dl_verify: String,
    val uan_verify: String,
    val face_verify: String,
    val address_verify: String,
    val branch: ImageBranch,
    val department: ImageDepartment
)

data class ImageBranch(
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

data class ImageDepartment(
    val id: Int,
    val company_id: Int,
    val name: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)
