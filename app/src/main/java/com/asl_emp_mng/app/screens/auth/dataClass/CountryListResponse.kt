package com.asl_emp_mng.app.screens.auth.dataClass

data class CountryListResponse(
    val data: ArrayList<DataCountry>,
    val message: String,
    val success: Boolean
)


data class DataCountry(
    val alpha_2_code: String,
    val alpha_3_code: String,
    val created_at: String,
    val currency_name: String,
    val currency_name_sf: String,
    val currency_symbol: String,
    val dial_code: String,
    val id: Int,
    val name: String,
    val status: String,
    val updated_at: String
)