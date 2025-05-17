package com.stafo.app.screens.crm.dataClass

data class UpdateLeadResponse(
    val success: Boolean,
    val message: String,
    val data: UpdateLeadData
)

data class UpdateLeadData(
    val id: Int,
    val company_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val notes: String,
    val status: String,
    val lead_from: String,
    val start_date: String?,
    val next_date: String,
    val created_at: String,
    val updated_at: String
)
