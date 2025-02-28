package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class VerifyRegisterNumberResponse(
    val data: VerifyRegisterCompanyData?,
    val status: String?,
    val message: String?,
    @SerializedName("request_id") val requestId: Int?,
    @SerializedName("status_code") val statusCode: Int?
)

data class VerifyRegisterCompanyData(
    val details: CompanyDetails?,
    @SerializedName("company_id") val companyId: String?,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("company_type") val companyType: String?
)

data class CompanyDetails(
    val charges: List<Any>?,
    val directors: List<Director>?,
    @SerializedName("company_info") val companyInfo: CompanyInfo?
)

data class Director(
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("din_number") val dinNumber: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("director_name") val directorName: String?,
    @SerializedName("surrendered_din") val surrenderedDin: String?
)

data class CompanyInfo(
    val cin: String?,
    @SerializedName("email_id") val emailId: String?,
    @SerializedName("roc_code") val rocCode: String?,
    @SerializedName("last_bs_date") val lastBsDate: String?,
    @SerializedName("last_agm_date") val lastAgmDate: String?,
    @SerializedName("listed_status") val listedStatus: String?,
    @SerializedName("company_status") val companyStatus: String?,
    @SerializedName("paid_up_capital") val paidUpCapital: String?,
    @SerializedName("class_of_company") val classOfCompany: String?,
    @SerializedName("company_category") val companyCategory: String?,
    @SerializedName("active_compliance") val activeCompliance: String?,
    @SerializedName("number_of_members") val numberOfMembers: String?,
    @SerializedName("status_under_cirp") val statusUnderCirp: String?,
    @SerializedName("authorized_capital") val authorizedCapital: String?,
    @SerializedName("registered_address") val registeredAddress: String?,
    @SerializedName("registration_number") val registrationNumber: String?,
    @SerializedName("company_sub_category") val companySubCategory: String?,
    @SerializedName("address_other_than_ro") val addressOtherThanRo: String?,
    @SerializedName("date_of_incorporation") val dateOfIncorporation: String?,
    @SerializedName("suspended_at_stock_exchange") val suspendedAtStockExchange: String?
)
