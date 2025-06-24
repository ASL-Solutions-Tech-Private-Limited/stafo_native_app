package com.stafo.app.screens.expense.dataClass

data class ExpenseFormCreateRequest(
    val company_id: String,
    val type_name: String,
    val description: String,
    val isDocumentReq: String,
    val fields: List<Field>
)

data class Field(
    val fieldName: String,
    val fieldType: String,
    val description: String
)
