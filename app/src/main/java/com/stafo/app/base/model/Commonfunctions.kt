package com.stafo.app.base.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date

object Commonfunctions {

    fun  checkConnectivity(context: Context):Boolean{
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result

    }

    open fun differanceInDays(toDate: String?, fromDate: String?): Int {
        return (((getFormattedDate(toDate).time - getFormattedDate(fromDate).time) / (24 * 60 * 60 * 1000)).toInt())
    }

    private fun getFormattedDate(date: String?): Date {
        return SimpleDateFormat("dd/MM/yyyy").parse(date)
    }
}