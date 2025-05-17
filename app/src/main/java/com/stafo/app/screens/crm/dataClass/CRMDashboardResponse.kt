package com.stafo.app.screens.crm.dataClass

data class CRMDashboardResponse(
    val data: Data?,
    val message: String?,
    val success: Boolean
)

data class Data(
    val total_followups: Int?,
    val total_followups_today: Int?,
    val total_followups_today_list: List<TotalFollowupsToday>?,
    val total_leads: Int?
)

data class Company(
    val company_code: String?,
    val company_name: String?,
    val email: String?,
    val id: Int?,
    val mobile_no: String?
)


data class TotalFollowupsToday(
    val company: Company?,
    val company_id: Int?,
    val created_at: String?,
    val email: String?,
    val id: Int?,
    val lead_from: String?,
    val name: String?,
    val next_date: String?,
    val notes: String?,
    val phone: Int?,
    val start_date: Any?,
    val status: String?,
    val updated_at: String?
)