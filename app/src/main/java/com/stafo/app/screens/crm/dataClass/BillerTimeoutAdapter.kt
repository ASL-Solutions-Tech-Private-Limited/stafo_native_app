package com.stafo.app.screens.crm.dataClass

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class BillerTimeoutAdapter : JsonDeserializer<List<String>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<String>? {
        return when {
            json.isJsonArray -> json.asJsonArray.mapNotNull { it.asString }
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> listOf(json.asString)
            else -> null
        }
    }
}
