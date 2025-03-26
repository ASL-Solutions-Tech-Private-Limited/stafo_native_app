package com.stafo.app.screens.settings.dataClass

data class JobTitleResponse(
    val status: Boolean,
    val message: String,
    val data: List<JobTitle>
)

data class JobTitle(
    val id: Int,
    val name: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)