package com.stafo.app.screens.performance.dataClass

data class PerformanceAddRequest(
    val emp_id: Int,
    val month: String,
    val year: String,
    val perform: List<DynamicPerformanceList>
)
