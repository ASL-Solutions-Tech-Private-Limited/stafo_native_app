package com.stafo.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class PunchInRequest(
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
)
