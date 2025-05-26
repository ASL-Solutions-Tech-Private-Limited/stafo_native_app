package com.stafo.app.base.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.stafo.app.api.ApiStores
import com.stafo.app.utils.CustomTrustManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object RetrofitInstance {

    private const val BASE_URL = "https://stafo.in/"
    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient(context))
                .build()
        }
        return retrofit!!
    }

    val apiService: ApiService by lazy {
        throw IllegalStateException("Use getApiService(context) instead")
    }

    fun getApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }
}


private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
    try {
        val trustManager = CustomTrustManager()
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), java.security.SecureRandom())

        val httpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true } // Disable hostname verification

        // Timeouts
        httpClient.readTimeout(ApiStores.READ_TIMEOUT, TimeUnit.SECONDS)
        httpClient.connectTimeout(ApiStores.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        httpClient.writeTimeout(ApiStores.WRITE_TIMEOUT, TimeUnit.SECONDS)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()

        httpClient.addInterceptor(loggingInterceptor)
        httpClient.addInterceptor(chuckerInterceptor)

        // Cache
        val cacheSize = 10 * 1024 * 1024 // 10MB
        val cache = Cache(context.cacheDir, cacheSize.toLong())
        httpClient.cache(cache)

        return httpClient.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}
