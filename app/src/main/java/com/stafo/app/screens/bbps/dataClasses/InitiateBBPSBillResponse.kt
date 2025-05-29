package com.stafo.app.screens.bbps.dataClasses


import com.google.gson.annotations.SerializedName

data class InitiateBBPSBillResponse(
    @SerializedName("data")
    var dataInitiatePayment: DataInitiatePayment?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)

data class DataInitiatePayment(
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("billFetchPost")
    var billFetchPost: String?,
    @SerializedName("BillType")
    var billType: String?,
    @SerializedName("cashBack")
    var cashBack: Int?,
    @SerializedName("deductibleAmount")
    var deductibleAmount: Int?,
    @SerializedName("mode")
    var mode: String?,
    @SerializedName("operator_code")
    var operatorCode: String?,
    @SerializedName("product_info")
    var productInfo: String?,
    @SerializedName("requestId")
    var requestId: String?,
    @SerializedName("txnid")
    var txnid: String?,
    @SerializedName("user_id")
    var userId: Int?,
    @SerializedName("user_type")
    var userType: String?
)