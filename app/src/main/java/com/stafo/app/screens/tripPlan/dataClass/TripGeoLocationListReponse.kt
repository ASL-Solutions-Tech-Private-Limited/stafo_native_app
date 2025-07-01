package com.stafo.app.screens.tripPlan.dataClass

data class TripGeoLocationListReponse(
    val status: Boolean,
    val message: String,
    val data: List<TripGeoLocation>
)

data class TripGeoLocation(
    val id: Int,
    val trip_id: Int,
    val company_id: Int,
    val latitude: String,
    val longitude: String,
    val created_at: String,
    val updated_at: String
)
