package com.stafo.app.screens.settings.dataClass

data class DayPunchINResponse(
    val status: Boolean,
    val message: String,
    val data: List<PunchData>
)

data class PunchData(
    val id: Int,
    val employee_id: Int,
    val punch_in: String,
    val punch_out: String,
    val created_at: String,
    val updated_at: String
)
