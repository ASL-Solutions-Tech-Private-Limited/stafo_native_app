package com.asl_emp_mng.app

import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.asl_emp_mng.app.api.ApiClient
import com.asl_emp_mng.app.api.ApiStores
import com.orhanobut.hawk.Hawk

class ASLEmpMng : MultiDexApplication() {
    init {
        instance = this
    }

    val fileName: String = "com.anddev.tourney11"

    companion object {
        lateinit var instance: ASLEmpMng
        lateinit var globalContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        globalContext = applicationContext
        Hawk.init(this).build()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
      //  FirebaseApp.initializeApp(this)

    }

    fun apiStores(): ApiStores? {
        return ApiClient.retrofit(applicationContext, BuildConfig.ENDPOINT)
            ?.create(ApiStores::class.java)
    }
}