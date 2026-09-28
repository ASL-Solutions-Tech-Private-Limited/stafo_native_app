package com.stafo.app.api


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.stafo.app.BuildConfig
import com.stafo.app.utils.CustomTrustManager
import com.stafo.app.utils.getDeviceId
import com.stafo.app.utils.getUserAccessToken
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import com.stafo.app.utils.getEMPDevice
import com.stafo.app.utils.getEmployeeDetails
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext


object ApiClient {

    var mRetrofit: Retrofit? = null
    private var retrofitStaging: Retrofit? = null

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


    /*private fun getUnsafeOkHttpClient(context: Context?): OkHttpClient {
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
    }*/


    private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager = CustomTrustManager()
            sslContext.init(null, arrayOf(trustManager), SecureRandom())

            val httpClient = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor { chain ->
                    val request = chain.request()
                    val path = request.url.encodedPath

                    val isAuthRequest = path.contains("login", ignoreCase = true) ||
                            path.contains("otp", ignoreCase = true) ||
                            path.contains("register", ignoreCase = true) ||
                            path.contains("verify", ignoreCase = true)

                    if (SessionManager.isLoggedOut && !isAuthRequest) {
                        throw IOException("Session expired. No further API calls allowed.")
                    }

                    val response = chain.proceed(request)

                    val oldDevice = getEMPDevice(context)
                    val currentDevice = Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )

                    Log.e("AuthDebug", "oldDevice: $oldDevice  currentDevice: $currentDevice code: ${response.code}")

                    if ((response.code == 401 || response.code == 403) && !isAuthRequest) {
                        Handler(Looper.getMainLooper()).post {
                            CustomToast(context, "Session expired. Please log in again.")
                        }
                        SessionManager.logout(context)
                    } else if (!isAuthRequest && !oldDevice.isNullOrBlank() && oldDevice != currentDevice) {
                        Handler(Looper.getMainLooper()).post {
                            CustomToast(context, "Device mismatch detected. You have been logged out.")
                        }
                        SessionManager.logout(context)
                    }

                    response
                }
                .readTimeout(ApiStores.READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(ApiStores.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiStores.WRITE_TIMEOUT, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val chuckInterceptor = ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()

                httpClient.addInterceptor(loggingInterceptor)
                httpClient.addInterceptor(chuckInterceptor)
            }

            val cacheSize = 10 * 1024 * 1024
            val cache = Cache(context.cacheDir, cacheSize.toLong())
            httpClient.cache(cache)

            return httpClient.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun retrofit(context: Context, apiServerUrl: String): Retrofit {
        if (mRetrofit == null) {
            mRetrofit = Retrofit.Builder()
                .baseUrl(apiServerUrl)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient(context))
                .build()
        }
        return mRetrofit!!
    }


  /*  fun retrofit(context: Context?, apiServerUrl: String): Retrofit? {

        if (mRetrofit == null && context != null) {
            mRetrofit = Retrofit.Builder()
                .baseUrl(apiServerUrl)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient(context))
                .build()
        }
        return mRetrofit
    }*/

    object SessionManager {
        @Volatile
        var isLoggedOut = false

        fun reset() {
            isLoggedOut = false
        }

        fun logout(context: Context) {
            if (!isLoggedOut) {
                isLoggedOut = true
                doLogout(context)
            }
        }
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
