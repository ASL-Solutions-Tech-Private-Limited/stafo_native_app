package com.stafo.app.screens.bbps.dataClasses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class InterchangeFeeCCF1Adapter :
    JsonDeserializer<List<BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1>> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1>? {

        return when {
            json.isJsonArray -> {
                json.asJsonArray.map { element ->
                    context.deserialize<BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1>(element, BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1::class.java)
                }
            }

            json.isJsonObject -> {
                listOf(
                    context.deserialize<BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1>(
                        json, BillerDetailsResponse.Data.MdmRequestNew.Biller.InterchangeFeeCCF1::class.java
                    )
                )
            }

            else -> null
        }
    }
}
