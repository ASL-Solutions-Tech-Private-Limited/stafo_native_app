package com.asl_emp_mng.app.api

import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiStores {

    companion object {
        //Timeout
        val READ_TIMEOUT: Long = 1200
        val CONNECT_TIMEOUT: Long = 1350
        val WRITE_TIMEOUT: Long = 1300
    }


    /**
     * ==============================================
     * API
     * ==============================================
     */


    @GET("api/company-types")
    suspend fun callCompanyType(): Response<CompanyTypeResponse>
}
