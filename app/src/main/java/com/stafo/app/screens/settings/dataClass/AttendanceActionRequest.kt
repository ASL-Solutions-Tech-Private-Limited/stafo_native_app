package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceActionRequest(
    val status: String,
    @SerializedName("reject_reason") val reject_reason: String? = null,
    val attendance: String? = null,
    val halfday: Int? = null
)
