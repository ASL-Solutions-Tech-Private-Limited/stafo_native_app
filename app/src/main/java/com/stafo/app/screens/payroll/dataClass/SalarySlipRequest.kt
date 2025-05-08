package com.stafo.app.screens.payroll.dataClass

import com.google.gson.annotations.SerializedName

data class SalarySlipRequest(
    @SerializedName("company_id")
    val companyId: String,

    @SerializedName("employee_id")
    val employeeId: String,

    val month: String,
    val year: String
)
