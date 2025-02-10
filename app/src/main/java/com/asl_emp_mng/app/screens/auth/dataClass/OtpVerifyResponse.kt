package com.asl_emp_mng.app.screens.auth.dataClass

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val data: OtpData
)
data class OtpData (
    val token: String,
    val company: Any? = null,
    val employee: Any? = null
)
