package com.stafo.app.screens.settings.dataClass

data class EditBranchResponse(
    val message: String,
    val data: EditBranchData
)

data class EditBranchData(
    val id: Int,
    val company_id: String,
    val branch_name: String,
    val branch_address: String,
    val latitude: String,
    val longitude: String,
    val radar: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)
