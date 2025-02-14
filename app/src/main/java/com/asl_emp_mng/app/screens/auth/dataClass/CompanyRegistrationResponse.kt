package com.asl_emp_mng.app.screens.auth.dataClass


import com.google.gson.annotations.SerializedName

data class CompanyRegistrationResponse(
    @SerializedName("companyData")
    var company: CompanyData,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("token")
    var token: String
)

data class CompanyData(
    @SerializedName("address")
    var address: String,
    @SerializedName("city")
    var city: Any,
    @SerializedName("company_name")
    var companyName: String,
    @SerializedName("company_type")
    var companyType: String,
    @SerializedName("country")
    var country: Any,
    @SerializedName("email")
    var email: String,
    @SerializedName("gst_number")
    var gstNumber: Any,
    @SerializedName("mobile_no")
    var mobileNo: String,
    @SerializedName("pan_number")
    var panNumber: Any,
    @SerializedName("pin")
    var pin: Any,
    @SerializedName("state")
    var state: Any
)