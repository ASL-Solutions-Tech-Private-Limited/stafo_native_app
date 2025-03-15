package com.stafo.app.screens.auth.dataClass

data class OtpResponse(
    val success: Boolean,
    val message: String,
    val otp: String
)
