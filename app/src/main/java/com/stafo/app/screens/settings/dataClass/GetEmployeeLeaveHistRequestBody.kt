package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class GetEmployeeLeaveHistRequestBody(
    @SerializedName("employee_id") val employeeId: String
)
