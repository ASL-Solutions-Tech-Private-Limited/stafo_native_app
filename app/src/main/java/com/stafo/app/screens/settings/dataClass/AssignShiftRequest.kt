package com.stafo.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class AssignShiftRequest(
    @SerializedName("shift_id") val shiftId: String,
    @SerializedName("employee_id") val employeeId: String
)

