package com.asl_emp_mng.app.screens.settings.dataClass

data class ViewBranchResponse(
    val message: String,
    val data: List<BranchItem>
)

data class BranchItem(
    val id: Int,
    val company_id: Int,
    val branch_name: String,
    val branch_address: String,
    val latitude: Double?,
    val longitude: Double?,
    val radar: Int,
    val status: Int,
    val created_at: String,
    val updated_at: String
)



