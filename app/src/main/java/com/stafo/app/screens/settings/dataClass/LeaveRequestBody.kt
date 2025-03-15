package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class LeaveRequestBody(
    @SerializedName("company_id") val companyId: String,
    @SerializedName("employee_id") val employeeId: String?
)