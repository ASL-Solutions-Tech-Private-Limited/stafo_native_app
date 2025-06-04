package com.stafo.app.screens.expense.dataClass

data class ExpenseCategory(
    val categoryName: String,
    val requiredFields: List<String>,
    val attachDocumentRequired: Boolean
)
