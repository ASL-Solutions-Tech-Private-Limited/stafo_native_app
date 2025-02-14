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
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AttendanceSummaryResponse
import com.asl_emp_mng.app.screens.settings.dataClass.BranchListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.HolidayListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ViewBranchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

   /* @POST("api/branch/create")
    suspend fun callCreateBranch(@Body addBranchRequest: AddBranchRequest): Response<AddBranchResponse>*/

    @POST("api/branch/create")
    suspend fun callCreateBranch(
        @Header("Authorization") token: String,
        @Body addBranchRequest: AddBranchRequest
    ): Response<AddBranchResponse>


    @POST("api/employees")
    suspend fun callAddEmp(
        @Header("Authorization") token: String,
        @Body addEmpRequest: AddEmpRequestBody
    ): Response<AddEmpResponse>

    @POST("api/shifts")
    suspend fun callCreateShift(@Header("Authorization") token: String, @Body request: ShiftCreateRequest): Response<ShiftCreateResponse>
    @GET("api/shifts")
    suspend fun callShiftList(@Header("Authorization") token: String): Response<ShiftListResponse>

    @GET("api/employees-list")
    suspend fun callEmployeeList(@Header("Authorization") token: String): Response<EmployeeListResponse>

    @GET("api/branch/list")
    suspend fun callBranchViewList(@Header("Authorization") token: String): Response<ViewBranchResponse>

    @GET("api/departments")
    suspend fun callDepartmentList( @Header("Authorization") token: String): Response<DepartmentResponse>
    @GET("api/branch/list")
    suspend fun callBranchList( @Header("Authorization") token: String): Response<BranchListResponse>


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

    @POST("api/holidays-create")
    suspend fun callCreateHoliday(@Header("Authorization") token: String, @Body request: CreateHolidayRequest): Response<CreateHolidayResponse>

    @POST("api/company-dashboard")
    suspend fun callCompanySummary( @Header("Authorization") token: String): Response<AttendanceSummaryResponse>

    @POST("api/leave-request")
    suspend fun callEmployeeLeaveRequest(@Header("Authorization") token: String, @Body request: EmployeeLeaveRequestBody): Response<EmployeeLeaveResponse>

    @GET("api/company/profile")
    suspend fun callCompanyProfile(@Header("Authorization") token: String): Response<CompanyProfileResponse>

    @GET("api/holidays/by-company")
    suspend fun callHolidayList(@Header("Authorization") token: String): Response<HolidayListResponse>

}
