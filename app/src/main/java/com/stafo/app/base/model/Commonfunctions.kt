package com.stafo.app.base.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date

object Commonfunctions {

    fun checkConnectivity(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    open fun differanceInDays(toDate: String?, fromDate: String?): Int {
        return (((getFormattedDate(toDate).time - getFormattedDate(fromDate).time) / (24 * 60 * 60 * 1000)).toInt())
    }

    private fun getFormattedDate(date: String?): Date {
        return SimpleDateFormat("dd/MM/yyyy").parse(date)
    }
}