package com.stafo.app.screens.subscription.dataClass

data class HashGenerateResponse(
    val hash: String?,
    val hashParam: HashParam?,
    val message: String?,
    val status: Boolean
)

data class HashParam(
    val amount: String?,
    val duration: Int?,
    val email: String?,
    val firstname: String?,
    val merchantKey: String?,
    val package_id: Int?,
    val phone: String?,
    val productinfo: String?,
    val salt: String?,
    val txnid: String?,
    val user_id: Int?
)