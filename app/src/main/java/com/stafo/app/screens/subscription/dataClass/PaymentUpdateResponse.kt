package com.stafo.app.screens.subscription.dataClass

data class PaymentUpdateResponse(
    val status: Boolean,
    val message: String,
    val expire_date: String
)
