package com.stafo.app.screens.settings.dataClass

data class SalaryGeneratedResponse(
    val success: Boolean, val message: String, val data: SalaryData
)

data class SalaryData(
    val employee_id: Int,
    val basic_salary: String,
    val earning: List<SalaryComponent>,
    val deduction: List<SalaryComponent>,
    val gross_salary: Int,
    val other_deduction: Int,
    val absent_days: Int,
    val working_days: Int,
    val expense: Int
)

data class SalaryComponent(
    val id: Int,
    val label: String,
    var amount: Int,
    val amount_type: String,
    val payment_type: String,
    val percentage: Int?

)

