package com.stafo.app.screens.billpayment

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stafo.app.screens.billpayment.dataClass.BillerInputParamsRaw
import com.stafo.app.screens.billpayment.dataClass.ParamInfo

fun BillerInputParamsRaw.toNormalized(): List<ParamInfo> {
    val gson = Gson()
    return when (paramInfo) {
        is Map<*, *> -> {
            // paramInfo is a single object
            listOf(gson.fromJson(gson.toJson(paramInfo), ParamInfo::class.java))
        }
        is List<*> -> {
            // paramInfo is already a list of objects
            (paramInfo as List<*>).mapNotNull {
                try {
                    gson.fromJson(gson.toJson(it), ParamInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }
        else -> emptyList()
    }
}
