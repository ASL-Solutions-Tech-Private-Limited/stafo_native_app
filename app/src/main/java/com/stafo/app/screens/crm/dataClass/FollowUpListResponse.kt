package com.stafo.app.screens.crm.dataClass

data class FollowUpListResponse(
    val success: Boolean,
    val message: String,
    val data: List<FollowUpItem>
)

data class FollowUpItem(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val lead_id: Int,
    val type: String,
    val next_date: String,
    val status: String,
    val remarks: String,
    val created_at: String,
    val updated_at: String,
    val company: FollowUpItemCompany,
    val employee: FollowUpItemEmployee
)

data class FollowUpItemCompany(
    val id: Int,
    val company_name: String,
    val company_code: String,
    val email: String,
    val mobile_no: String
)

data class FollowUpItemEmployee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String
)

