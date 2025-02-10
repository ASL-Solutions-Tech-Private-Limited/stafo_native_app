package com.asl_emp_mng.app.api

import com.asl_emp_mng.app.base.request.OtpRequestBody
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.base.request.RegisterRequest
import com.asl_emp_mng.app.base.request.VerifyOtpRequestBody
import com.asl_emp_mng.app.screens.auth.dataClass.AddBranchResponse
import com.asl_emp_mng.app.screens.auth.dataClass.BusinessTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CitiesListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CountryListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.LoginResponse
import com.asl_emp_mng.app.screens.auth.dataClass.OtpResponse
import com.asl_emp_mng.app.screens.auth.dataClass.OtpVerifyResponse
import com.asl_emp_mng.app.screens.auth.dataClass.RegisterResponse
import com.asl_emp_mng.app.screens.auth.dataClass.StatesListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    @GET("api/business-types")
    suspend fun callBusinessType(): Response<BusinessTypeResponse>

    @GET("api/countries")
    suspend fun callCountryList(): Response<CountryListResponse>

    @GET("api/countries/{country_id}/states")
    suspend fun callStatesList(@Path("country_id") countryId: String): Response<StatesListResponse>


    @GET("api/states/{state_id}/cities")
    suspend fun callCityList(@Path("state_id") stateId: String): Response<CitiesListResponse>
    @POST("api/branch/create")
    suspend fun callCreateBranch(@Body addBranchRequest: AddBranchRequest): Response<AddBranchResponse>
    @POST("api/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("api/send-otp")
    suspend fun sendOtp(@Body request: OtpRequestBody): Response<OtpResponse>

    @POST("api/verify-otp")
    suspend fun verifyUserOtp(@Body request: VerifyOtpRequestBody): Response<OtpVerifyResponse>
    @POST("api/login")
    suspend fun userLogin(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<LoginResponse>

}
