package com.stafo.app.screens.bbps.dataClasses


import com.google.gson.annotations.JsonAdapter
import com.stafo.app.screens.crm.dataClass.BillerAdditionalInfoAdapter
import com.stafo.app.screens.crm.dataClass.BillerTimeoutAdapter

data class BillerDetailsResponse(
    val success: Boolean, val message: String, val data: Data?
) {
    data class Data(
        val refid: String?, val message: String?, val mdmRequestNew: MdmRequestNew?
    ) {
        data class MdmRequestNew(
            val responseCode: String?, val biller: Biller?
        ) {
            data class Biller(
                val billerId: String?,
                val billerAliasName: String?,
                val billerName: String?,
                val billerCategory: String?,
                val billerAdhoc: String?,
                val billerCoverage: String?,
                val billerFetchRequiremet: String?,
                @JsonAdapter(BillerPaymentExactnessAdapter::class)
                val billerPaymentExactness: List<String>?,
                val billerSupportBillValidation: String?,
                val supportPendingStatus: String?,
                val supportDeemed: String?,
                val billerStatus: String?,

                @JsonAdapter(BillerTimeoutAdapter::class)
                val billerTimeout: List<String>?,

                val billerInputParams: BillerInputParams?,

                @JsonAdapter(BillerAdditionalInfoAdapter::class)
                val billerAdditionalInfo: List<BillerAdditionalInfoUnion>?,

                val billerAmountOptions: String?,
                val billerPaymentModes: String?,
                @JsonAdapter(BillerDescriptionAdapter::class)
                val billerDescription: List<String>?,

                @JsonAdapter(RechargeAmountInValidationRequestAdapter::class)
                val rechargeAmountInValidationRequest: List<String>?,

                val billerPaymentChannels: BillerPaymentChannels?,
                val billerAdditionalInfoPayment: List<Any>?,
                val planAdditionalInfo: List<Any>?,
                val planMdmRequirement: Any?,
                val billerResponseType: Any?,
                val billerPlanResponseParams: List<Any>?,
                @JsonAdapter(InterchangeFeeCCF1Adapter::class)
                val interchangeFeeCCF1: List<InterchangeFeeCCF1>?
            ) {
                data class BillerInputParams(
                    val paramInfo: List<ParamInfo>?
                ) {
                    data class ParamInfo(
                        val paramName: String?,
                        val dataType: String?,
                        val isOptional: String?,
                        val minLength: String?,
                        val maxLength: String?,
                        val regEx: String?,
                        val visibility: String? = null,
                        val values: String? = null

                    )
                }

                data class BillerAdditionalInfoUnion(
                    @JsonAdapter(ParamInfoAdapter::class) val paramInfo: List<ParamInfo>?
                ) {
                    data class ParamInfo(
                        val paramName: String?
                    )
                }

                data class BillerPaymentChannels(
                    val paymentChannelInfo: List<PaymentChannelInfo>?
                ) {
                    data class PaymentChannelInfo(
                        val paymentChannelName: String?,
                        val minAmount: String?,
                        val maxAmount: String?
                    )
                }

                data class InterchangeFeeCCF1(
                    val feeCode: String?,
                    val feeDirection: String?,
                    val flatFee: String?,
                    val percentFee: String?,
                    val feeMinAmt: String?,
                    val feeMaxAmt: String?
                )
            }
        }
    }
}


