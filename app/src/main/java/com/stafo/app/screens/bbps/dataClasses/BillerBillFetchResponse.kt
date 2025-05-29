package com.stafo.app.screens.bbps.dataClasses

import com.google.gson.annotations.JsonAdapter

data class BillerBillFetchResponse(
    val success: Boolean,
    val message: String,
    val data: Data?
) {
    data class Data(
        val status: Int,
        val xml: String,
        val datar: String,
        val message: String,
        val billData: BillData,
        val refid: String
    ) {
        data class BillData(
            val responseCode: String,
            @JsonAdapter(InputParamsAdapter::class)
            val inputParams: InputParams,
            val billerResponse: BillerResponse,
          //  @JsonAdapter(AdditionalInfoPramAdapter::class)
            val additionalInfo: AdditionalInfo?
        ) {
            data class InputParams(
                val input: List<Input>
            ) {
                data class Input(
                    val paramName: String,
                    val paramValue: String
                )
            }

            data class BillerResponse(
                val amountOptions: AmountOptions,
                val billAmount: String,
                val billDate: String,
                val billNumber: String,
                val billPeriod: String,
                val customerName: String,
                val dueDate: String
            ) {
                data class AmountOptions(
                    val option: List<Option>
                ) {
                    data class Option(
                        val amountName: String,
                        val amountValue: String
                    )
                }
            }

            data class AdditionalInfo(
                val info: List<Info>
            ) {
                data class Info(
                    val infoName: String,
                    val infoValue: String
                )
            }
        }
    }
}
