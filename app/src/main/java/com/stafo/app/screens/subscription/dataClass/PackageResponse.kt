package com.stafo.app.screens.subscription.dataClass

data class PackageResponse(
    val status: Boolean,
    val message: String,
    val data: List<PackageData>
)

data class PackageData(
    val id: Int,
    val package_name: String,
    val description: String,
    val price: String,
    val discount_price: String,
    val days: Int,
    val status: String,
    val created_at: String,
    val updated_at: String,
    val features: List<Feature>
)

data class Feature(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String?, // nullable
    val status: Int,
    val created_at: String,
    val updated_at: String,
    val pivot: Pivot
)

data class Pivot(
    val package_id: Int,
    val features_id: Int,
    val feature_value: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

