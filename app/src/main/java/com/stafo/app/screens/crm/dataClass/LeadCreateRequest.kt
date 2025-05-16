package com.stafo.app.screens.crm.dataClass

import com.google.gson.annotations.SerializedName

data class LeadCreateRequest(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("employee_id")
    val employeeId: String,
    val name: String,
    val company_name: String,
    val email: String,
    val phone: String,
    val notes: String,
    val status: String,
    @SerializedName("lead_from")
    val leadFrom: String,
    @SerializedName("next_date")
    val nextDate: String
)
