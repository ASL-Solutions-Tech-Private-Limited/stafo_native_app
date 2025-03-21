package com.stafo.app.screens.settings.dataClass

data class BannerResponse(
    val status: Boolean,
    val data: BannerData
)

data class BannerData(
    val image: String,
    val title: String,
    val path: String,
    val imageurl: String
)
