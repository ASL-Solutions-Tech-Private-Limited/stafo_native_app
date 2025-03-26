package com.stafo.app.api


import android.content.Context
import com.stafo.app.BuildConfig
import com.stafo.app.utils.CustomTrustManager
import com.stafo.app.utils.getDeviceId
import com.stafo.app.utils.getUserAccessToken
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext


object ApiClient {

    var mRetrofit: Retrofit? = null
    var mRetrofitForGame: Retrofit? = null
    var mRetrofitForCSP: Retrofit? = null
    private val device_type = "device_type"
    private val device_token = "device_token"
    private val Authorization = "Authorization"
    private val DEVICE_TYPE = "Android"
    private val Accept = "Accept"
    private val Accept_type = "application/json"
    private val Content_key = "Content-Type"
    private val Content_value = "application/json"
    private val API_KEY = "api-key"
    private val device_id = "device_id"
    private val Access_token = "access-token"
    val MOBILE_VERIFIED = "MOBILE_VERIFIED"
    var HTTP_STATUS_CODE_200 = 200
    var HTTP_STATUS_CODE_201 = 201
    var HTTP_STATUS_CODE_UNAUTHENTICATED_401 = 401


    private fun getUnsafeOkHttpClient(context: Context?): OkHttpClient {
        try {
            // Create an SSL context with a custom TrustManager that trusts the self-signed certificate
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(CustomTrustManager()), java.security.SecureRandom())

            // Create an OkHttpClient with a custom SSLSocketFactory
            val httpClient = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, CustomTrustManager())
                .hostnameVerifier { hostname, session -> true } // Bypass hostname verification for simplicity

            // Timeout settings
            httpClient.readTimeout(ApiStores.READ_TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(ApiStores.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            httpClient.writeTimeout(ApiStores.WRITE_TIMEOUT, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                // Logging
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val chuckInterceptor =
                    ChuckerInterceptor.Builder(context!!)
                        .collector(ChuckerCollector(context!!))
                        .maxContentLength(250000L)
                        .redactHeaders(emptySet())
                        .alwaysReadResponseBody(false)
                        .build()

                httpClient.addInterceptor(loggingInterceptor)
                httpClient.addInterceptor(chuckInterceptor)

            }

            // Cache
            val cacheSize = 10 * 1024 * 1024 // 10 MiB
            val cache = Cache(context?.cacheDir!!, cacheSize.toLong())
            httpClient.cache(cache)

            return httpClient.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun retrofit(context: Context?, apiServerUrl: String): Retrofit? {

        if (mRetrofit == null && context != null) {
            mRetrofit = Retrofit.Builder()
                .baseUrl(apiServerUrl)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient(context))
                .build()
        }
        return mRetrofit
    }





    fun headerMap(): HashMap<String, String> {
        val headermap = HashMap<String, String>()
        headermap["Authorization"] = "Bearer ${getUserAccessToken()}"
        // headermap[Content_key] = Content_value
        return headermap
    }

    fun headerDeviceIDMap(context: Context?): HashMap<String, String> {
        val headermap = HashMap<String, String>()
        headermap["deviceId"] = "${getDeviceId(context!!)}"
        // headermap[Content_key] = Content_value
        return headermap
    }

}
