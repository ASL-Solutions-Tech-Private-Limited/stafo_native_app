package com.stafo.app.screens.performance.dataClass


data class PerformanceTypeResponse(
    val success: Boolean,
    val message: String,
    val data: List<PerformanceTypeList>
)

data class PerformanceTypeList(
    val id: Int,
    val company_id: Int,
    val name: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)
