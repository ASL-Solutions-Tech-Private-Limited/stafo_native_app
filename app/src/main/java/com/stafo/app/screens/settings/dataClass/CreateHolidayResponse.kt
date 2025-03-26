package com.stafo.app.screens.settings.dataClass

data class CreateHolidayResponse(
    val status: Boolean,
    val message: String,
    val data: List<HolidayData>
)
data class HolidayData(
    val id: Int,
    val company_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val created_at: String? = null,
    val updated_at: String? = null
)
