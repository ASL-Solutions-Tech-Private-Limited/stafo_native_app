package com.stafo.app.screens.billpayment.dataClass

import com.google.gson.*
import java.lang.reflect.Type

data class BillerInputParamsRaw(
    val paramInfo: List<ParamInfo>
) {
    fun BillerInputParamsRaw.toNormalized(): List<ParamInfo> {
        val gson = Gson()
        return when (paramInfo) {
            is List<*> -> {
                (paramInfo as List<*>).mapNotNull {
                    gson.fromJson(gson.toJson(it), ParamInfo::class.java)
                }
            }
            is Map<*, *> -> {
                listOf(gson.fromJson(gson.toJson(paramInfo), ParamInfo::class.java))
            }
            else -> emptyList()
        }
    }
}

