package com.stafo.app.screens.settings.dataClass

data class PanVerifyRequestBody(
    val company_id:String,
    val type:String,
    val number: String,
    val otp: String? = null,
    val request_id: String? = null,
    val dob: String? = null,

)
