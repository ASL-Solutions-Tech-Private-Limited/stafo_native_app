package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceSummaryResponse(
    @SerializedName("employeeCount") val employeeCount: Int,
    @SerializedName("presentCount") val presentCount: Int
)
