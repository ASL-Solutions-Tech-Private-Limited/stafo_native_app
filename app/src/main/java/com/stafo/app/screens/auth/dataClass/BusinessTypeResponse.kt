package com.stafo.app.screens.auth.dataClass


data class BusinessTypeResponse(
    val success: Boolean,
    val data: ArrayList<DataBusinessType>




)

data class DataBusinessType(
    val business_name: String,
    val created_at: String,
    val id: Int,
    val status: Int,
    val updated_at: String
)