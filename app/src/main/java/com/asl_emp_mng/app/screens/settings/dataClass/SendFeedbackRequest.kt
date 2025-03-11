package com.asl_emp_mng.app.screens.settings.dataClass

data class SendFeedbackRequest(
    val company_id:String? = null,
    val employee_id: String? = null,
    val message:String
)
