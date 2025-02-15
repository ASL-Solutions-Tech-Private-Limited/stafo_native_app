package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName


data class UpdateCompanyProfile(
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_type") val companyType: String,
    @SerializedName("business_type_id") val businessTypeId: Int,
    @SerializedName("registration_number") val registrationNumber: String,
    @SerializedName("gst_number") val gstNumber: String,
    @SerializedName("pan_number") val panNumber: String,
    @SerializedName("address") val address: String,
    @SerializedName("city_id") val cityId: Int,
    @SerializedName("state_id") val stateId: Int,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("pin") val pin: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("ifsc_code") val ifscCode: String,
    @SerializedName("no_of_employee") val noOfEmployee: Int,
    @SerializedName("status") val status: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile_no") val mobileNo: String,
    @SerializedName("owner_info") val ownerInfo: OwnerInfo
)

data class OwnerInfo(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String
)
