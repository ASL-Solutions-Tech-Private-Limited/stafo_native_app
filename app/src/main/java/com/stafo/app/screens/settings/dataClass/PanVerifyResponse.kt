package com.stafo.app.screens.settings.dataClass


data class PanVerifyResponse(
    val data: Data?,
    val status: String?,
    val message: String?,
    val request_id: String?,
    val status_code: Int?
)

data class Data(
    val category: String?,
    val full_name: String?,
    val pan_number: String?
)