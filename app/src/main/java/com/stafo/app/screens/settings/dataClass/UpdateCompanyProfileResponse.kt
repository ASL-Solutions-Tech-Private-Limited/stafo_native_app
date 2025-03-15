package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class UpdateCompanyProfileResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CompanyDataUpdate
)

data class CompanyDataUpdate(
    @SerializedName("company") val company: CompanyUpdate,
    @SerializedName("country_name") val countryName: String?,
    @SerializedName("state_name") val stateName: String?,
    @SerializedName("city_name") val cityName: String?
)

data class CompanyUpdate(
    @SerializedName("id") val id: Int,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_code") val companyCode: String,
    @SerializedName("proprietor_id") val proprietorId: Any?,
    @SerializedName("company_type") val companyType: String,
    @SerializedName("business_type_id") val businessTypeId: Int,
    @SerializedName("registration_number") val registrationNumber: String,
    @SerializedName("gst_number") val gstNumber: String,
    @SerializedName("pan_number") val panNumber: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: Any?, // Nullable type
    @SerializedName("state") val state: Any?, // Nullable type
    @SerializedName("country") val country: Any?, // Nullable type
    @SerializedName("pin") val pin: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("ifsc_code") val ifscCode: String,
    @SerializedName("no_of_employee") val noOfEmployee: Int,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("mobile_no") val mobileNo: String,
    @SerializedName("otp") val otp: Any?, // Nullable type
    @SerializedName("email") val email: String
)

