package com.stafo.app.screens.settings.dataClass

data class SalaryTypeRequest(
    val company_id: Int,
    val payment_type: String,
    val salary_type: String,
    val salary_type_description: String,
    val amount: String,
    val amount_type: String
)
