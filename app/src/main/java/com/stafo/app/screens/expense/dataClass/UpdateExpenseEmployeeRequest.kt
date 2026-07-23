package com.stafo.app.screens.expense.dataClass

data class UpdateExpenseEmployeeRequest(
    val company_id: Int,
    val employee_id: Int,
    val amount: String,
    val status: String,
    val expense_details: List<UpdateExpenseDetailRequest>
)

data class UpdateExpenseDetailRequest(
    val expense_id: Int,
    val expenseform_id: Int,
    val expense_value: String
)
