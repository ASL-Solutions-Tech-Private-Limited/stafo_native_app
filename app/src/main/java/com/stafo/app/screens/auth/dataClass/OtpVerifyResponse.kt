package com.stafo.app.screens.auth.dataClass

import com.stafo.app.screens.settings.dataClass.Employee

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val data: OtpData?
)
data class OtpData (
    val token: String,
    val company: CompanyData? = null,
    val employee: Employee? = null,
    val device_id: String?,
    val device_name: String?,
    val device_version: String?,
    val device_change: String?
)


