package com.stafo.app.screens.payroll.dataClass

import com.stafo.app.screens.settings.dataClass.SalaryComponent

data class SalaryRequest(
    val company_id: Int,
    val employee_id: Int,
    val month: Int,
    val basic_salary: Double,
    val gross_salary: Double,
    val other_deduction: Double,
    val absent_days: Int,
    val working_days: Int,
    val expense: Double,
    val components: MutableList<SalaryComponent>
)

data class SalaryComponent(
    val id: Int,
    val label: String,
    val amount: Int,
    val amount_type: String,
    val payment_type: String
)

