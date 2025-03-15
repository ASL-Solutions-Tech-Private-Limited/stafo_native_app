package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName


data class GeoLocationHistResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<GeoLocation>
)

data class GeoLocation(
    @SerializedName("id") val id: Int,
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)