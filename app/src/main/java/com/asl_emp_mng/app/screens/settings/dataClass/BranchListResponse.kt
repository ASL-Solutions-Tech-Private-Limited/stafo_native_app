package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class BranchListResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<DataBranch>
)

data class DataBranch(
    @SerializedName("id") val id: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("branch_name") val branch_name: String,
    @SerializedName("branch_address") val branchAddress: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("radar") val radar: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

