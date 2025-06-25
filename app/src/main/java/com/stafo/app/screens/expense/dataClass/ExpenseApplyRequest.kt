package com.stafo.app.screens.expense.dataClass

data class ExpenseApplyRequest(
    val company_id: Int,
    val employee_id: Int,
    val amount: String,
    val expensetype_id: Int,
    val expense_details: List<ApplyExpenseDetailRequest>
)
