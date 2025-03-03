package com.asl_emp_mng.app.screens.settings.dataClass

data class CompanyAcceptDeviceRequest(
    val employee_id: Int,
    val status: String,
    val device_id: String
)
