package com.stafo.app.screens.payroll.dataClass

data class AllReportsListResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReportFile>
)

data class ReportFile(
    val file_name: String,
    val download_url: String,
    val file_type: String,
    val created_at: String,
    val exported_date: String

)