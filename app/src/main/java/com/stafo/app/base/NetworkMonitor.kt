package com.stafo.app.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkMonitor : BroadcastReceiver() {

    private val _isConnected = MutableLiveData<Boolean>(true)
    val isConnected: LiveData<Boolean> = _isConnected

    fun register(application: Application) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        application.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            _isConnected.postValue(isInternetAvailable(it))
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = cm.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
}