package com.stafo.app.screens.expense.dataClass

import com.stafo.app.screens.crm.dataClass.LeadEmployee

data class GetApplyExpenseListResponse(
    val status: Boolean,
    val message: String,
    val data: List<GetExpenseList>

)
data class GetExpenseList(
    val id: Int,
    val employee_id: Int,
    val employee: String,
    val expenseType: String,
    val date: String,
    val amount: String,
    val status: String
)


