package com.stafo.app.screens.crm.dataClass

data class CreateFollowUpRequest(
    val company_id: String,
    val employee_id: String,
    val lead_id: String,
    val type: String,
    val next_date: String,
    val status: String,
    val remarks: String
)
