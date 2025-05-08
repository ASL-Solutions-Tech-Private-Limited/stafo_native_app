package com.stafo.app.screens.billpayment.dataClass

data class CategoryMenuResponse(
    val success: Boolean,
    val data: List<Category>
)

data class Category(
    val id: Int,
    val name: String,
    val code: String,
    val img: String?,
    val status: Int,
    val icon_url:String?
)