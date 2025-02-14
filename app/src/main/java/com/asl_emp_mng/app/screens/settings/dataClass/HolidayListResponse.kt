package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName
data class HolidayListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Holiday>
)

data class Holiday(
    val id: Int,
    val company_id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val created_at: String? = null,
    val updated_at: String? = null
)


