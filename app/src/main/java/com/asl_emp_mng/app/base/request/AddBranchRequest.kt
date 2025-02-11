package com.asl_emp_mng.app.base.request

import com.google.gson.annotations.SerializedName

data class AddBranchRequest(
    @SerializedName("company_id")
    var company_id: Int,
    @SerializedName("branch_name")
    var branch_name: String,
    @SerializedName("branch_address")
    var branch_address: String,

    @SerializedName("latitute")
    var latitute: String,

    @SerializedName("longtitute")
    var longtitute: String,

    @SerializedName("radar")
    var radar: String
)
