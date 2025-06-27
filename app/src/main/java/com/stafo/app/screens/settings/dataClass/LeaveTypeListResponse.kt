package com.stafo.app.screens.settings.dataClass

import java.io.Serializable

data class LeaveTypeListResponse(
    val success: Boolean,
    val message: String,
    val data: List<LeaveItem>
)

data class LeaveItem(
    val id: Int,
    val company_id: Int,
    val name: String,
    val no_of_days: Int,
    val description: String,
    val is_paid:Int,
    val status: Int,
    val created_at: String,
    val updated_at: String
): Serializable
