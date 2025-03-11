package com.asl_emp_mng.app.screens.settings.dataClass

data class SendFeedbackResponse(
    val status: Boolean,
    val message: String,
    val data: FeedbackData
)
data class FeedbackData(
    val admin_id: String,
    val company_id: String?,
    val employee_id: String?,
    val message: String,
    val message_by: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)