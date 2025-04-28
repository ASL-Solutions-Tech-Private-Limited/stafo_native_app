package com.stafo.app.screens.billpayment.dataClass

data class ElectricityOperatorResponse(
    val success: Boolean,
    val message: String,
    val data: List<ElectricityOperator>
)

data class ElectricityOperator(
    val name: String,
    val operator_code: String
)