package com.asl_emp_mng.app.screens.auth.dataClass

data class AddBranchResponse(
    val message: String,
    val data: BranchData
)
data class BranchData(
    val branch_address: String,
    val branch_name: String,
    val company_id: Int,
    val updated_at: String,
    val created_at: String,
    val id: Int
)