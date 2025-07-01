package com.stafo.app.screens.settings.dataClass

data class SalaryGeneratedResponse(
    val success: Boolean,
    val message: String,
    val data: SalaryData
)

data class SalaryData(
    val employee_id: Int,
    val basic_salary: String,
    val earning: List<SalaryComponent>,
    val deduction: List<SalaryComponent>,
    val gross_salary: Int
)

data class SalaryComponent(
    val id: Int,
    val label: String,
    var amount: Int,
    val amount_type: String,
    val payment_type: String,
    val percentage: Int?

)

/*data class SalaryPreviewResponse(
    val success: Boolean,
    val message: String,
    val data: SalaryPreviewData
)

data class SalaryPreviewData(
    val basic_salary: String,
    val earning: List<SalaryComponent>,
    val deduction: Int,
    val other_deduction: Int,
    val gross_salary: Int
)

data class SalaryComponent(
    val id: Int,
    val label: String,
    val amount: Int,
    val amount_type: String,
    val payment_type: String,
    val percentage: Int? // nullable because it's null in some cases
)*/
