package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class CheckAvailabilityStatusResponse(
    @SerializedName("available")
    var available: Boolean?,
    @SerializedName("status")
    var status: Boolean?
)