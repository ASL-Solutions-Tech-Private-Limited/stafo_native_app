package com.stafo.app.screens.crm.dataClass

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.stafo.app.screens.bbps.dataClasses.BillerDetailsResponse
import java.lang.reflect.Type

class BillerAdditionalInfoAdapter :
    JsonDeserializer<List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion>? {
        return when {
            json.isJsonArray -> {
                json.asJsonArray.mapNotNull {
                    context.deserialize(
                        it,
                        BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion::class.java
                    )
                }
            }

            json.isJsonObject -> listOf(
                context.deserialize(
                    json,
                    BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion::class.java
                )
            )

            else -> null
        }
    }
}
