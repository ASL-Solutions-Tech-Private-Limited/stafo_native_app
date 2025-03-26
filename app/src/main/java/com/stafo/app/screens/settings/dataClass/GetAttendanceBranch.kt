package com.stafo.app.screens.settings.dataClass


data class GetAttendanceBranch(
    val status: Boolean,
    val message: String,
    val data: BranchData
)

data class BranchData(
    val id: Int,
    val company_id: Int,
    val branch_name: String,
    val branch_address: String,
    val latitude: String?,
    val longitude: String?,
    val radar: Int,
    val status: Int,
    val created_at: String,
    val updated_at: String
)