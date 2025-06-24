package com.stafo.app.screens.expense.dataClass

import java.io.Serializable

data class GetAllExpenseFormList(
    val success: Boolean,
    val message: String,
    val data: List<ExpenseFormTypeList>
)
data class ExpenseFormTypeList(
    val id: Int,
    val company_id: Int,
    val name: String,
    val description: String,
    val is_document_req: String,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val expense_forms: List<ExpenseFormList>
): Serializable

data class ExpenseFormList(
    val id: Int,
    val company_id: Int,
    val type_id: Int,
    val field_name: String,
    val field_type: String,
    val description: String?,
    val status: String,
    val created_at: String,
    val updated_at: String
):Serializable