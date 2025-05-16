package com.stafo.app.screens.crm.dataClass

data class CreateFollowUpResponse(
    val success: Boolean,
    val message: String,
    val data: FollowUpData
)

data class FollowUpData(
    val company_id: String,
    val employee_id: String,
    val lead_id: String,
    val type: String,
    val next_date: String,
    val status: String,
    val remarks: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
