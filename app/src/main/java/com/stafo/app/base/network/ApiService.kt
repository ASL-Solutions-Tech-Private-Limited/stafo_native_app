package com.stafo.app.base.network

import com.stafo.app.api.ApiClient
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationResponse
import com.stafo.app.screens.settings.dataClass.LocationLogRequest
import com.stafo.app.screens.settings.dataClass.LocationLogResponse
import com.stafo.app.screens.tripPlan.dataClass.TripGeoLocationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {
    @POST("api/store-geo-location")
    suspend fun callPostGeoLocation(
        @Header("Authorization") token: String,
        @Body request: EmployeePostLocationRequest
    ): Response<EmployeePostLocationResponse>

    @POST("api/devicelog-store")
    suspend fun callDeviceLog(
        @Body request: LocationLogRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<LocationLogResponse>

    @POST("api/trips-geolocation/create")
    suspend fun callTripGeoLocation(
        @Body request: TripGeoLocationRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<LocationLogResponse>
}