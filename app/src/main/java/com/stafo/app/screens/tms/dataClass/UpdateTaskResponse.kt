package com.stafo.app.screens.tms.dataClass

import com.google.gson.annotations.SerializedName

data class UpdateTaskResponse(
    val success: Boolean,
    val message: String,
    val data: UpdateTaskData
)

data class UpdateTaskData(
    val id: Int,
    @SerializedName("company_id")
    val companyId: Int,
    val title: String,
    val description: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    val status: String,
    val priority: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)