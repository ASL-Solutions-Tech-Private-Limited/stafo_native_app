package com.asl_emp_mng.app.screens.auth.dataClass

import com.google.gson.annotations.SerializedName

data class SelfieAttendanceResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("similarity") val similarity: Double,
    @SerializedName("message") val message: String
)
