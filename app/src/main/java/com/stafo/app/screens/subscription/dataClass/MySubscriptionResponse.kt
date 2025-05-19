package com.stafo.app.screens.subscription.dataClass

data class MySubscriptionResponse(
    val status: Boolean,
    val message: String,
    val downloadUrl: String,
    val data: SubscriptionData?
)

data class SubscriptionData(
    val id: Int,
    val company_name: String,
    val package_id: Int,
    val package_price: Double,
    val subscription_start: String,
    val subscription_end: String,
    val `package`: PackageDetail
)

data class PackageDetail(
    val id: Int,
    val package_name: String,
    val description: String,
    val price: String,
    val discount_price: String,
    val days: Int,
    val status: String,
    val created_at: String,
    val updated_at: String
)