package com.stafo.app.screens.settings.dataClass

data class SalaryTypeResponse(
    val success: Boolean,
    val message: String,
    val data: SalaryTypeData
)

data class SalaryTypeData(
    val company_id: Int,
    val payment_type: String,
    val salary_type: String,
    val salary_type_description: String,
    val amount: String,
    val amount_type: String,
    val status: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)