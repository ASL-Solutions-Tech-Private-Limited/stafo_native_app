package com.stafo.app

import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.stafo.app.api.ApiClient
import com.stafo.app.api.ApiStores
import com.mmi.services.account.MapmyIndiaAccountManager
import com.orhanobut.hawk.Hawk
import com.stafo.app.base.NetworkConnectivityHandler
import com.stafo.app.base.NetworkMonitor
import com.stafo.app.utils.scheduleDailyEndOfDaySync

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
        scheduleDailyEndOfDaySync(applicationContext)
        registerActivityLifecycleCallbacks(NetworkConnectivityHandler(this))

        initMapMyIndia()

    }

    fun apiStores(): ApiStores? {
        return ApiClient.retrofit(applicationContext, BuildConfig.ENDPOINT)
            ?.create(ApiStores::class.java)
    }




    private fun initMapMyIndia() {
        MapmyIndiaAccountManager.getInstance().setRestAPIKey("b99061448178b709d1b24054f7ea218d")
        MapmyIndiaAccountManager.getInstance().setMapSDKKey("b99061448178b709d1b24054f7ea218d")
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType("client_credentials")
        MapmyIndiaAccountManager.getInstance().setAtlasClientId("96dHZVzsAuveHJyb4fsrVuXD0YNPrFaochM2cB-f7hG7DijsK6wuIGwWgAo7ksFFxTVpPm2mORP_XLz9OkWc1Q==")
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret("lrFxI-iSEg9UFw9ZECaYSUPOvunPyH3qtIQyBP0lo-8yMBn9fNnEUP8xU44RbKPf-yq4d7x-H1T6fo1qyZRdt7x6r4gib2ys")
    }
}