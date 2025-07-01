package com.stafo.app.screens.expense.dataClass

data class ExpenseCategory(
    val id: Int,
    val categoryName: String,
    val requiredFields: List<String>,
    val attachDocumentRequired: Boolean
)

