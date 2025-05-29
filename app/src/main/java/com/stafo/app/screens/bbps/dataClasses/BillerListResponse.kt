package com.stafo.app.screens.bbps.dataClasses


import com.google.gson.annotations.SerializedName

data class BillerListResponse(
    @SerializedName("data")
    var dataBiller: List<DataBiller>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)

data class DataBiller(
    @SerializedName("category")
    var category: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("operator_code")
    var operatorCode: String?,
    @SerializedName("service")
    var service: String?
)