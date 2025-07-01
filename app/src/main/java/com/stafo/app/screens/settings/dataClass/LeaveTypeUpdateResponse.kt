package com.stafo.app.screens.settings.dataClass

data class LeaveTypeUpdateResponse(
    val message: String,
    val success: Boolean,
    val data: UpdateLeaveItem
)

data class UpdateLeaveItem(
    val id: Int,
    val company_id: Int,
    val name: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)
