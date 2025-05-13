package com.stafo.app.screens.subscription.dataClass

import org.json.JSONObject

data class PaymentResponse(
    val expire_date: String?,
    val message: String?,
    val status: Boolean
)

