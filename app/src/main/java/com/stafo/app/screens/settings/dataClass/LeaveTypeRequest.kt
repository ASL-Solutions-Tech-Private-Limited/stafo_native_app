package com.stafo.app.screens.settings.dataClass

data class LeaveTypeRequest(
    val company_id: Int? = null,
    val name: String,
    val no_of_days: Int,
    val description: String,
    val is_paid: String
)
