package com.stafo.app.screens.settings.dataClass

data class CompanyAcceptDeviceRequest(
    val employee_id: Int,
    val status: String,
    val device_id: String
)
