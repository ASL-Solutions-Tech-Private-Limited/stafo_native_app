package com.asl_emp_mng.app.screens.auth.dataClass

data class CompanyTypeResponse(
    val data: List<DataCompanyType>?,
    val success: Boolean
)

data class DataCompanyType(
    val company_name: String,
    val created_at: String,
    val id: Int,
    val status: String,
    val updated_at: String
)