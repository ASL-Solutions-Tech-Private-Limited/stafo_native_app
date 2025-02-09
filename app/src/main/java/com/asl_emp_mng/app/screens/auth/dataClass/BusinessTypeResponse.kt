package com.asl_emp_mng.app.screens.auth.dataClass

data class BusinessTypeResponse(
    val dataBusinessType: List<DataBusinessType>,
    val success: Boolean
)

data class DataBusinessType(
    val business_name: String,
    val created_at: String,
    val id: Int,
    val status: Int,
    val updated_at: String
)