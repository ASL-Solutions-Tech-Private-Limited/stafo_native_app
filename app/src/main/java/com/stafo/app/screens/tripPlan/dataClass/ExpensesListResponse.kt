package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class ExpensesListResponse(
    @SerializedName("data")
    var dataExpensesList: List<DataExpenses>?,
    @SerializedName("success")
    var success: Boolean?
)


data class DataExpenses(
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("expense_type")
    var expenseType: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("note")
    var note: String?,
    @SerializedName("trip_id")
    var tripId: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?
)