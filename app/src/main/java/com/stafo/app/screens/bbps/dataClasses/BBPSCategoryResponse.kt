package com.stafo.app.screens.bbps.dataClasses


import com.google.gson.annotations.SerializedName

data class BBPSCategoryResponse(
    @SerializedName("data")
    var dataCategory: List<DataCategory>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)


data class DataCategory(
    @SerializedName("category_code")
    var categoryCode: String?,
    @SerializedName("category_icon")
    var categoryIcon: String?,
    @SerializedName("name")
    var name: String?
)