package com.asl_emp_mng.app.screens.settings.dataClass

data class PolicyFetchResponse(
    val status: Boolean,
    val message: String,
    val file_path: String,
    val data: List<Policy>
)

data class Policy(
    val id: Int,
    val company_id: Int,
    val title: String,
    val description: String,
    val file: String,
    val created_at: String,
    val updated_at: String
)
