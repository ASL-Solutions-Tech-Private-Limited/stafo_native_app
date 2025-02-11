package com.asl_emp_mng.app.screens.settings.dataClass

data class BranchListResponse(
    val message: String,
    val data: ArrayList<DataBranch>
)

data class DataBranch(
    val id: Int,
    val company_id: Int,
    val branch_name: String,
    val branch_address: String,
    val status: Int,
    val created_at: String,
    val updated_at: String
)

