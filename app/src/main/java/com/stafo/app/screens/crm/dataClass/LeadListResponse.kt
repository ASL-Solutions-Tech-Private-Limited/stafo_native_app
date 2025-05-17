package com.stafo.app.screens.crm.dataClass

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeadListResponse(
    val data: List<LeadData>?,
    val message: String?,
    val success: Boolean
)

data class LeadData(
    val company: LeadCompany?,
    val company_id: Int?,
    val created_at: String?,
    val email: String?,
    val employee: LeadEmployee?,
    val employee_id: Int?,
    val id: Int?,
    val lead_from: String?,
    val name: String?,
    val next_date: String?,
    val notes: String?,
    val phone: Int?,
    val start_date: Any?,
    val status: String?,
    val updated_at: String?
) : Serializable

data class LeadCompany(
    val company_code: String?,
    val company_name: String?,
    val email: String?,
    val id: Int?,
    val mobile_no: String?
) : Serializable

data class LeadEmployee(
    val email: String?,
    val emp_id: String?,
    val id: Int?,
    val name: String?,
    val phone: String?
) : Serializable