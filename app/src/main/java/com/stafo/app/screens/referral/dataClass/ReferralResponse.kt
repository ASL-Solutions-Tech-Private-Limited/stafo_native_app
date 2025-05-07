package com.stafo.app.screens.referral.dataClass

data class ReferralResponse(
    val status: Boolean,
    val message: String,
    val referral_code: String,
    val referral_count: Int,
    val referral_list: List<ReferralItem>
)

data class ReferralItem(
    val id: Int,
    val company_name: String,
    val company_code: String,
    val email: String,
    val mobile_no: String
)

