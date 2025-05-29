package com.stafo.app.screens.bbps.dataClasses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ParamInfoAdapter :
    JsonDeserializer<List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion.ParamInfo>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion.ParamInfo> {
        return when {
            json.isJsonArray -> {
                json.asJsonArray.map {
                    context.deserialize(it, BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion.ParamInfo::class.java)
                }
            }
            json.isJsonObject -> {
                listOf(context.deserialize(json, BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerAdditionalInfoUnion.ParamInfo::class.java))
            }
            else -> emptyList()
        }
    }
}
