package com.stafo.app.screens.subscription.dataClass

data class PaymentUpdateRequest(
    val company_id: String,
    val duration: String,
    val packageId: String,
    val amount: String,
    val txnid: String,
    val status: String,
    val payment_Message: String,
    val payment_info: Map<String, Any>
)
