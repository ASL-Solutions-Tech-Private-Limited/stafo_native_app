package com.stafo.app.screens.subscription.dataClass


import com.google.gson.annotations.SerializedName

data class CheckPaymentStatusRequest(
    @SerializedName("category")
    var category: String?,
    @SerializedName("operator_code")
    var operatorCode: String?,
    @SerializedName("postdata")
    var postdata: Postdata?,
    @SerializedName("transactionResponse")
    val transactionResponse: Map<String, Any>,
    @SerializedName("txnId")
    var txnId: String?,
    @SerializedName("user_id")
    var userId: String?,
    @SerializedName("user_type")
    var userType: String?
)
data class Postdata(
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("billPaymentRequestxml")
    var billPaymentRequestxml: String?,
    @SerializedName("mode")
    var mode: String?,
    @SerializedName("requestId")
    var requestId: String?
)

data class TransactionResponse(
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("bank_ref_num")
    var bankRefNum: String?,
    @SerializedName("bankcode")
    var bankcode: String?,
    @SerializedName("card_type")
    var cardType: String?,
    @SerializedName("cardnum")
    var cardnum: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("error")
    var error: String?,
    @SerializedName("error_Message")
    var errorMessage: String?,
    @SerializedName("firstname")
    var firstname: String?,
    @SerializedName("hash")
    var hash: String?,
    @SerializedName("key")
    var key: String?,
    @SerializedName("mihpayid")
    var mihpayid: String?,
    @SerializedName("mode")
    var mode: String?,
    @SerializedName("paymentId")
    var paymentId: Int?,
    @SerializedName("pg_TYPE")
    var pgTYPE: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("productinfo")
    var productinfo: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("txnid")
    var txnid: String?,
    @SerializedName("unmappedstatus")
    var unmappedstatus: String?
)