package com.stafo.app.screens.bbps.dataClasses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class InputParamsAdapter : JsonDeserializer<BillerBillFetchResponse.Data.BillData.InputParams> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BillerBillFetchResponse.Data.BillData.InputParams {
        val jsonObject = json.asJsonObject
        val inputElement = jsonObject["input"]

        val inputList = when {
            inputElement.isJsonArray -> {
                inputElement.asJsonArray.map {
                    context.deserialize<BillerBillFetchResponse.Data.BillData.InputParams.Input>(it, BillerBillFetchResponse.Data.BillData.InputParams.Input::class.java)
                }
            }
            inputElement.isJsonObject -> {
                listOf(context.deserialize<BillerBillFetchResponse.Data.BillData.InputParams.Input>(inputElement, BillerBillFetchResponse.Data.BillData.InputParams.Input::class.java))
            }
            else -> emptyList()
        }

        return BillerBillFetchResponse.Data.BillData.InputParams(inputList)
    }
}

