package com.stafo.app.screens.auth.dataClass

data class StatesListResponse(
    val data: ArrayList<DataStates>,
    val message: String,
    val success: Boolean
)

data class DataStates(
    val country_id: Int,
    val created_at: String,
    val id: Int,
    val iso_code: String,
    val name: String,
    val status: String,
    val updated_at: String
)