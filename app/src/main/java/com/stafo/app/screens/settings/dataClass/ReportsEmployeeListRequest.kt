package com.stafo.app.screens.settings.dataClass

data class ReportsEmployeeListRequest(
    val month: Int,
    val year: Int,
    val format: String,
    val company_id: String,
    val department: Int,
    val branch: Int
)
