package com.stafo.app.base.network

import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/store-geo-location")
    suspend fun callPostGeoLocation(
        @Header("Authorization") token: String,
        @Body request: EmployeePostLocationRequest
    ): Response<EmployeePostLocationResponse>
}