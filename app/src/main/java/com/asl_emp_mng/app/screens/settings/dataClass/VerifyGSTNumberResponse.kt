package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class VerifyGSTNumberResponse(
    val data: GstData?,
    val status: String?,
    val message: String?,
    @SerializedName("request_id") val requestId: Int?,
    @SerializedName("status_code") val statusCode: Int?
)

data class GstData(
    val gstin: String?,
    val address: String?,
    @SerializedName("hsn_info") val hsnInfo: Map<String, Any>?,
    @SerializedName("legal_name") val legalName: String?,
    @SerializedName("pan_number") val panNumber: String?,
    @SerializedName("gstin_status") val gstinStatus: String?,
    @SerializedName("business_name") val businessName: String?,
    @SerializedName("filing_status") val filingStatus: List<List<FilingStatus>>?,
    @SerializedName("taxpayer_type") val taxpayerType: String?,
    @SerializedName("address_details") val addressDetails: Map<String, Any>?,
    @SerializedName("einvoice_status") val einvoiceStatus: Boolean?,
    @SerializedName("filing_frequency") val filingFrequency: List<Any>?,
    @SerializedName("aadhaar_validation") val aadhaarValidation: String?,
    @SerializedName("state_jurisdiction") val stateJurisdiction: String?,
    @SerializedName("center_jurisdiction") val centerJurisdiction: String?,
    @SerializedName("date_of_cancellation") val dateOfCancellation: String?,
    @SerializedName("date_of_registration") val dateOfRegistration: String?,
    @SerializedName("field_visit_conducted") val fieldVisitConducted: String?,
    @SerializedName("nature_bus_activities") val natureBusActivities: List<String>?,
    @SerializedName("aadhaar_validation_date") val aadhaarValidationDate: String?,
    @SerializedName("constitution_of_business") val constitutionOfBusiness: String?,
    @SerializedName("nature_of_core_business_activity_code") val coreBusinessActivityCode: String?,
    @SerializedName("nature_of_core_business_activity_description") val coreBusinessActivityDescription: String?
)

data class FilingStatus(
    val status: String?,
    @SerializedName("tax_period") val taxPeriod: String?,
    @SerializedName("return_type") val returnType: String?,
    @SerializedName("date_of_filing") val dateOfFiling: String?,
    @SerializedName("financial_year") val financialYear: String?,
    @SerializedName("mode_of_filing") val modeOfFiling: String?
)
