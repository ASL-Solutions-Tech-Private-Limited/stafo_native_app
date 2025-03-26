package com.stafo.app.base.model


import com.google.gson.annotations.SerializedName

data class RequestGeoLocationResponse(
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)