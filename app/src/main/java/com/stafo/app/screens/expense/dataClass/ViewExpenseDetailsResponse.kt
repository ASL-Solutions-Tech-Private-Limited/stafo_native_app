package com.stafo.app.screens.expense.dataClass

import com.google.gson.annotations.SerializedName


data class ViewExpenseDetailsResponse(
    val success: Boolean,
    val message: String,
    val data: ExpenseData
)

data class ExpenseData(
    val id: Int,
    val company_id: Int,
    val employee_id: Int,
    val expensetype_id: Int,
    val amount: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val expense_type: ExpenseType,
    val expense_details: List<ExpenseDetail>,
    val attachments: List<Any>
)

data class ExpenseType(
    val id: Int,
    val name: String,
    val description: String?
)

data class ExpenseDetail(
    val id: Int,
    val expense_id: Int,
    val expenseform_id: Int,
    val expense_value: String,
    @SerializedName("expense_form_details")
    val expense_form: ExpenseForm?
)

data class ExpenseForm(
    val id: Int,
    val field_name: String,
    val description: String?
)

