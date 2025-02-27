package com.asl_emp_mng.app.base.request


data class VerifyOtpRequestBody(
    val mobile_number: String,
    val otp: String,
    val device_id: String

)
