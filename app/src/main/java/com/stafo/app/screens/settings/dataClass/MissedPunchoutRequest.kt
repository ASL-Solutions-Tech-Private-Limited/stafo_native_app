package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class MissedPunchoutRequest(
    @SerializedName("employee_id") val employee_id: Int,
    val date: String,
    @SerializedName("punch_out_time") val punch_out_time: String,
    val reason: String
)
