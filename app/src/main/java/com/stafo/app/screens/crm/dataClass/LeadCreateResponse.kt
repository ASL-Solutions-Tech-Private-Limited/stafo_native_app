package com.stafo.app.screens.crm.dataClass

data class LeadCreateResponse(
    val success: Boolean,
    val data: CreateLeadData?,
    val message: String?
)
data class CreateLeadData(
    val company_id: String?,
    val created_at: String?,
    val email: String?,
    val employee_id: String?,
    val id: Int?,
    val lead_from: String?,
    val name: String?,
    val next_date: String?,
    val notes: String?,
    val phone: String?,
    val status: String?,
    val updated_at: String?
)