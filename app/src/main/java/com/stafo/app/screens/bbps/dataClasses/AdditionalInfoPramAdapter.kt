package com.stafo.app.screens.bbps.dataClasses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


class AdditionalInfoPramAdapter :
    JsonDeserializer<BillerBillFetchResponse.Data.BillData.AdditionalInfo> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BillerBillFetchResponse.Data.BillData.AdditionalInfo {
        val jsonObject = json.asJsonObject
        val inputElement = jsonObject["info"]

        val inputList = when {
            inputElement.isJsonArray -> {
                inputElement.asJsonArray.map {
                    context.deserialize<BillerBillFetchResponse.Data.BillData.AdditionalInfo.Info>(
                        it,
                        BillerBillFetchResponse.Data.BillData.AdditionalInfo.Info::class.java
                    )
                }
            }

            inputElement.isJsonObject -> {
                listOf(
                    context.deserialize<BillerBillFetchResponse.Data.BillData.AdditionalInfo.Info>(
                        inputElement,
                        BillerBillFetchResponse.Data.BillData.AdditionalInfo.Info::class.java
                    )
                )
            }

            else -> emptyList()
        }

        return BillerBillFetchResponse.Data.BillData.AdditionalInfo(inputList)
    }
}