package com.asl_emp_mng.app.screens.auth.dataClass


import com.google.gson.annotations.SerializedName

data class CompanyRegistrationResponse(
    @SerializedName("company")
    var company: CompanyData?,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("token")
    var token: String
)

data class CompanyData(
    @SerializedName("company_name") val companyName: String = "",
    @SerializedName("company_type") val companyType: String = "",
    @SerializedName("gst_number") val gstNumber: String? = null,
    @SerializedName("pan_number") val panNumber: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("pin") val pin: String? = null,
    @SerializedName("mobile_no") val mobileNo: String = "",
    @SerializedName("email") val email: String = ""
)