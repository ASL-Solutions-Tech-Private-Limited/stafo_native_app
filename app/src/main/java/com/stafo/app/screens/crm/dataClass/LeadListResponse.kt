package com.stafo.app.screens.crm.dataClass

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeadListResponse(
    val success: Boolean,
    val message: String,
    val data: List<LeadData>
)

data class LeadData(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val name: String,
    val company_name: String,
    val company_address: String?, // nullable
    val email: String,
    val phone: Long,
    val notes: String,
    val status: String,
    val lead_from: String,
    val start_date: String?, // nullable
    val next_date: String,
    val created_at: String,
    val updated_at: String,
    val company: LeadCompany,
    val employee: LeadEmployee
) : Serializable

data class LeadCompany(
    val id: Int,
    val company_name: String,
    val company_code: String?, // nullable
    val email: String,
    val mobile_no: String
) : Serializable

data class LeadEmployee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String
) : Serializable