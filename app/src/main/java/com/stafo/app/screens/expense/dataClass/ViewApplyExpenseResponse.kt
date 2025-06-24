package com.stafo.app.screens.expense.dataClass

import java.io.Serializable

data class ViewApplyExpenseResponse(
    val success: Boolean,
    val message: String,
    val data: List<ApplyExpenseData>
)
data class ApplyExpenseData(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val amount: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val expense_details: List<ApplyExpenseDetail>
):Serializable
data class ApplyExpenseDetail(
    val id: Int,
    val expense_id: Int,
    val expenseform_id: Int,
    val expense_value: String,
    val created_at: String,
    val updated_at: String
):Serializable