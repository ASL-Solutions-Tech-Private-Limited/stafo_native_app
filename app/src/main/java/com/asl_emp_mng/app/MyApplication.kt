package com.asl_emp_mng.app

import android.app.Application
import com.mmi.MapmyIndiaMapView


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

       /* // Initialize MapMyIndia SDK with API keys
        MapmyAcc.getInstance().setRestAPIKey(getRestAPIKey())
        MapmyIndiaAccountManager.getInstance().setMapSDKKey(getMapSDKKey())
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType(getAtlasGrantType())
        MapmyIndiaAccountManager.getInstance().setAtlasClientId(getAtlasClientId())
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(getAtlasClientSecret())*/
    }

    private fun getRestAPIKey(): String {
        return "YOUR_REST_API_KEY"
    }

    private fun getMapSDKKey(): String {
        return "YOUR_MAP_SDK_KEY"
    }

    private fun getAtlasGrantType(): String {
        return "client_credentials" // As mentioned in the documentation
    }

    private fun getAtlasClientId(): String {
        return "YOUR_ATLAS_CLIENT_ID"
    }

    private fun getAtlasClientSecret(): String {
        return "YOUR_ATLAS_CLIENT_SECRET"
    }
}
