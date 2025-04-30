package com.stafo.app.screens.performance.dataClass


data class AddPerformanceResponse(
    val success: Boolean,
    val message: String,
    val data: RecordData
)

data class RecordData(
    val company_id: Int,
    val name: String,
    val description: String,
    val status: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)