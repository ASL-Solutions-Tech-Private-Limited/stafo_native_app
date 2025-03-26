package com.stafo.app.screens.settings.dataClass

data class DepartmentCreateResponse(
    val success: Boolean,
    val message: String,
    val data: DepartmentData
)

data class DepartmentData(
    val company_id: Int,
    val name: String,
    val description: String?,
    val status: Int,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
