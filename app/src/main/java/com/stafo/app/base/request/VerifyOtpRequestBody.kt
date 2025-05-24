package com.stafo.app.base.request


data class VerifyOtpRequestBody(
    val mobile_number: String,
    val otp: String,
    val device_id: String,
    var firebase_token: String = "",
    var device_name: String = "",
    var android_version: String = ""

)
