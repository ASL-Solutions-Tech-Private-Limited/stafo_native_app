package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceActionRequest(
    @SerializedName("request_id") val request_id: Int,
    val status: String,
    @SerializedName("reject_reason") val reject_reason: String? = null,
    @SerializedName("reject_attendance_type") val reject_attendance_type: String? = null
)
