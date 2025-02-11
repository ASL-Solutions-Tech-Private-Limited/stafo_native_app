package com.asl_emp_mng.app.screens.auth.dataClass
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)

data class Data(
    val token: String,
    val company: Company
)

data class Company(
    val id: Int,
    val company_name: String,
    val proprietor_id: String?,
    val company_type: String,
    val business_type_id: String?,
    val registration_number: String,
    val gst_number: String,
    val pan_number: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val pin: String,
    val bank_name: String?,
    val account_number: String?,
    val ifsc_code: String?,
    val no_of_employee: Int,
    val status: String?,
    val created_at: String,
    val updated_at: String,
    val mobile_no: String,
    val otp: String?,
    val email: String
)
