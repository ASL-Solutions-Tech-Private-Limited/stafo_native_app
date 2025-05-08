package com.stafo.app.screens.billpayment

import com.google.gson.*
import com.stafo.app.screens.billpayment.dataClass.BbpsOperatorDetailsResponse
import java.lang.reflect.Type

class BbpsOperatorDetailsDeserializer : JsonDeserializer<BbpsOperatorDetailsResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BbpsOperatorDetailsResponse {
        return Gson().fromJson(json, BbpsOperatorDetailsResponse::class.java)
    }
}