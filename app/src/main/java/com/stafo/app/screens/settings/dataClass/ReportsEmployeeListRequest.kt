package com.stafo.app.screens.settings.dataClass

data class ReportsEmployeeListRequest(
    val start_date: String,
    val end_date: String,
    val format: String,
    val company_id: String,
    val department: Int,
    val branch: Int
)
