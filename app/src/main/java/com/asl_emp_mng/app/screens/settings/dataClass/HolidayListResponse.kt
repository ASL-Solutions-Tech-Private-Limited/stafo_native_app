package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class HolidayListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Holiday>
)

data class Holiday(
    val id: Int,
    @SerializedName("company_id") val companyId: Int,
    val title: String,
    val description: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

