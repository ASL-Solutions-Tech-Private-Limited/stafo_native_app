package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class CreateHolidayResponse(
    val status: Boolean,
    val message: String,
    val data: HolidayData?
)

data class HolidayData(
    val title: String,
    val description: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
    val id: Int
)