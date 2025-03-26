package com.stafo.app.screens.auth.dataClass

import java.util.ArrayList

data class CompanyTypeResponse(
    val data: ArrayList<DataCompanyType>?,
    val success: Boolean
)

data class DataCompanyType(
    val company_name: String,
    val created_at: String,
    val id: Int,
    val status: String,
    val updated_at: String
)