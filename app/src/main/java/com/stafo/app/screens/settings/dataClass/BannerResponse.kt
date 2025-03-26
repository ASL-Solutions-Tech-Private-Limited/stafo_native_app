package com.stafo.app.screens.settings.dataClass

data class BannerResponse(
    val status: Boolean,
    val data: BannerData
)

data class BannerData(
    val banner: List<Banner>,
    val path: String
)

data class Banner(
    val id: Int,
    val title: String,
    val image: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)
