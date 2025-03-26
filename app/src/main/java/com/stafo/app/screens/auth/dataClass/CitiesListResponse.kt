package com.stafo.app.screens.auth.dataClass

data class CitiesListResponse(
    val data: ArrayList<DataCity>,
    val message: String,
    val success: Boolean
)

data class DataCity(
    val created_at: String,
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String,
    val updated_at: String
)

