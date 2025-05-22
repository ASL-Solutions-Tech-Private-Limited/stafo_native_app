package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AddEmpRequestBody(
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("date_of_joianing")
    val dateOfJoining: String,
    val gender: String,
    val address: String,
    val salary: String,
    @SerializedName("shift_ids")
    val shiftIds: List<String>
)
