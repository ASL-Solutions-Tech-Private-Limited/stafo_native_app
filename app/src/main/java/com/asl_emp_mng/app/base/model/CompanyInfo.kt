package com.asl_emp_mng.app.base.model

import java.io.Serializable

data class CompanyInfo(
    val company_name: String,
    val company_type: String,
    val business_type: String,
    val registration_number: String,
    val gst_number: String,
    val pan_number: String,
    val mobile_no: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val country: String,
    val state: String,
    val city: String,
    val address: String,
    val pin: String
): Serializable
