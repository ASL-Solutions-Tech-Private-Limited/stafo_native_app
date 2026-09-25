package com.stafo.app.screens.bbps.dataClasses


import com.google.gson.annotations.SerializedName

data class PromoCodeListResponse(
    @SerializedName("data")
    var dataPromoList: List<DataPromo>?,
    @SerializedName("success")
    var success: Boolean?
)

data class DataPromo(
    @SerializedName("applicable")
    var applicable: String?,
    @SerializedName("cashback")
    var cashback: String?,
    @SerializedName("code")
    var code: String?,
    @SerializedName("expires_at")
    var expiresAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("is_active")
    var isActive: Boolean?,
    @SerializedName("max_amount")
    var maxAmount: Int?,
    @SerializedName("min_amount")
    var minAmount: Int?,
    @SerializedName("type")
    var type: String?
)