package com.asl_emp_mng.app.screens.auth.dataClass

data class CompanyRegisterResponse(
    val status: Boolean,
    val message: String,
    val data: CompanyData
)

data class CompanyData(
    val proprietor_id: String,
    val company_name: String,
    val company_type: String,
    val registration_number: String?,
    val gst_number: String?,
    val pan_number: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val pin: String?,
    val bank_name: String?,
    val account_number: String?,
    val ifsc_code: String?,
    val no_of_employee: Int,
    val status: String?,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
