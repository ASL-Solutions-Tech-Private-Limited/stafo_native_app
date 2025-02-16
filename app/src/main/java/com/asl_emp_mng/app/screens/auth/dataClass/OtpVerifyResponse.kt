package com.asl_emp_mng.app.screens.auth.dataClass

import com.asl_emp_mng.app.screens.settings.dataClass.Employee
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeData

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val data: OtpData?
)
data class OtpData (
    val token: String,
    val company: CompanyData? = null,
    val employee: Employee? = null
)
