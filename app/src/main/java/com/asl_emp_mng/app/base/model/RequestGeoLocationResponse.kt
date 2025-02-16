package com.asl_emp_mng.app.base.model


import com.google.gson.annotations.SerializedName

data class RequestGeoLocationResponse(
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)