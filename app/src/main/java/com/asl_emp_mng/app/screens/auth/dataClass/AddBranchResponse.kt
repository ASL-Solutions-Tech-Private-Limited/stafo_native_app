package com.asl_emp_mng.app.screens.auth.dataClass

data class AddBranchResponse(
    val message: String,
    val data: ArrayList<DataBusinessType>
)

data class DataBranchType(
    val company_id: String,
    val branch_name: String,
    val branch_address: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)