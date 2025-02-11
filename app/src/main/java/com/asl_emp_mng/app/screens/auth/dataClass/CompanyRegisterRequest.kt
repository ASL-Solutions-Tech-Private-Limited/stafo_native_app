package com.asl_emp_mng.app.screens.auth.dataClass

data class CompanyRegisterRequest(
    val proprietor_id: String,
    val company_name: String,
    val company_type: String,
    val no_of_employee : String
)
