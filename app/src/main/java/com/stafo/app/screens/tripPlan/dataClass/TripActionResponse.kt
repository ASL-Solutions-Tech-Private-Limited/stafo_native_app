package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class TripActionResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)