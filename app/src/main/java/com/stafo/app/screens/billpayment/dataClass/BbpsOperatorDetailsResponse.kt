package com.stafo.app.screens.billpayment.dataClass



data class BbpsOperatorDetailsResponse(
    val status: Int,
    val refid: String,
    val message: String,
    val mdmRequestNew: MdmRequestNew
)

data class MdmRequestNew(
    val responseCode: String,
    val biller: Biller
)

data class Biller(
    val billerId: String,
    val billerName: String,
    val billerCategory: String,
    val billerAdhoc: String,
    val billerCoverage: String,
    val billerFetchRequiremet: String,
    val billerPaymentExactness: String,
    val billerSupportBillValidation: String,
    val supportPendingStatus: String,
    val supportDeemed: String,
    val billerTimeout: List<Any>,
    val billerInputParams: BillerInputParamsRaw,
    val billerAdditionalInfo: List<Any>,
    val billerAmountOptions: String,
    val billerPaymentModes: String,
    val billerDescription: List<Any>,
    val rechargeAmountInValidationRequest: List<Any>,
    val billerPaymentChannels: BillerPaymentChannels,
    val billerAdditionalInfoPayment: List<Any>,
    val planAdditionalInfo: List<Any>,
    val planMdmRequirement: List<Any>
)



data class ParamInfo(
    val paramName: String,
    val dataType: String?,
    val isOptional: String?,
    val minLength: String?,
    val maxLength: String?,
    val regEx: String?,
    val visibility: String? = null
)

data class BillerPaymentChannels(
    val paymentChannelInfo: List<PaymentChannelInfo>
)

data class PaymentChannelInfo(
    val paymentChannelName: String,
    val minAmount: String,
    val maxAmount: String
)
