package com.stafo.app.screens.settings.dataClass

data class SalaryTypeListResponse(
    val success: Boolean,
    val message: String,
    val data: List<SalaryType>
)

data class SalaryType(
    val id: Int,
    val company_id: Int,
    val payment_type: String,
    val salary_type: String,
    val salary_type_description: String,
    val amount: Int,
    val amount_type: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)