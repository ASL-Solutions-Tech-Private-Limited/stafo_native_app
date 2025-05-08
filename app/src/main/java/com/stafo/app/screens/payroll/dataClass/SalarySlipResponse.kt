package com.stafo.app.screens.payroll.dataClass

data class SalarySlipResponse(
    val success: Boolean,
    val message: String,
    val data: SalarySlipData
)

data class SalarySlipData(
    val filename: String,
    val download_url: String
)
