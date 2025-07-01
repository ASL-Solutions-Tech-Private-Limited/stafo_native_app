package com.stafo.app.screens.expense.dataClass

data class UpdateExpenseResponse(
    val message: String,
    val success: Boolean,
    val data: UpdatedExpenseData
)

data class UpdatedExpenseData(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val amount: Int,
    val status: String,
    val created_at: String,
    val updated_at: String
)
