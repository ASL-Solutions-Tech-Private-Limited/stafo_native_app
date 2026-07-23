package com.stafo.app.screens.settings.dataClass

data class LeaveTypeResponse(
    val success: Boolean,
    val message: String,
    val data: GetLeaveData
)

data class GetLeaveData(
    val company_id: String,
    val name: String,
    val description: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
