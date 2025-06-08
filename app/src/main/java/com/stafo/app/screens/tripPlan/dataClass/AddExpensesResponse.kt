package com.stafo.app.screens.tripPlan.dataClass

import com.google.gson.annotations.SerializedName


data class AddExpensesResponse(
    @SerializedName("data")
    var dataExpensesList: DataExpenses?,
    @SerializedName("success")
    var success: Boolean?,
    @SerializedName("message")
    var message: String
)



