package com.stafo.app.screens.rank.dataClass

data class PointsResponse(
    val success: Boolean,
    val message: String,
    val data: List<PerformanceRecord>,
    val month: String,
    val year: String
)

data class PerformanceRecord(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val performance_type_id: Int,
    val marks: Int,
    val month: Int,
    val year: Int,
    val created_at: String,
    val updated_at: String,
    val performancetype: PerformanceType
)

data class PerformanceType(
    val id: Int,
    val name: String,
    val description: String?
)
