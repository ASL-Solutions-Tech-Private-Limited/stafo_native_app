package com.asl_emp_mng.app.api

import com.asl_emp_mng.app.base.model.EmployeeDashboardResponse
import com.asl_emp_mng.app.base.model.MonthAttendaceResponse
import com.asl_emp_mng.app.base.model.RequestGeoLocationResponse
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.base.request.OtpRequestBody
import com.asl_emp_mng.app.base.request.RegisterRequest
import com.asl_emp_mng.app.base.request.VerifyOtpRequestBody
import com.asl_emp_mng.app.screens.auth.dataClass.AddBranchResponse
import com.asl_emp_mng.app.screens.auth.dataClass.BusinessTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CitiesListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyRegistrationResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CountryListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.LoginResponse
import com.asl_emp_mng.app.screens.auth.dataClass.OtpResponse
import com.asl_emp_mng.app.screens.auth.dataClass.OtpVerifyResponse
import com.asl_emp_mng.app.screens.auth.dataClass.SelfieAttendanceResponse
import com.asl_emp_mng.app.screens.auth.dataClass.StatesListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AssignShiftRequest
import com.asl_emp_mng.app.screens.settings.dataClass.AttendanceSummaryResponse
import com.asl_emp_mng.app.screens.settings.dataClass.BranchListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ChangeDeviceRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ChangeDeviceResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyAcceptDeviceRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyUpdateDocumentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyViewRequestDevice
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyViewRequestDeviceResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DayPunchINRequest
import com.asl_emp_mng.app.screens.settings.dataClass.DayPunchINResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DeleteResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentCreateRequest
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentCreateResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeDocumentUploadResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeePostLocationResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeUploadImageResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeViewDocumentRequest
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeViewDocumentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.FetchEmployeeDetails
import com.asl_emp_mng.app.screens.settings.dataClass.GeoLocationHistResponse
import com.asl_emp_mng.app.screens.settings.dataClass.GeoLocationHistResquest
import com.asl_emp_mng.app.screens.settings.dataClass.GetAllEmployeeResponse
import com.asl_emp_mng.app.screens.settings.dataClass.GetAttendanceRecordRequest
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmpAttendanceRecord
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmpAttendanceRecordBody
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployeeLeaveHistRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.GetEmployeeLeaveHistResponse
import com.asl_emp_mng.app.screens.settings.dataClass.HolidayListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.JobTitleResponse
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.LeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.OnLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PanVerifyRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.PanVerifyResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PendingLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PolicyCreateResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PolicyFetchResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PunchInRequest
import com.asl_emp_mng.app.screens.settings.dataClass.PunchInResponse
import com.asl_emp_mng.app.screens.settings.dataClass.QRAttendanceMarkRequest
import com.asl_emp_mng.app.screens.settings.dataClass.QRAttendanceMarkResponse
import com.asl_emp_mng.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.asl_emp_mng.app.screens.settings.dataClass.SetAttendanceTypeResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftAssignmentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateCompanyProfile
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateCompanyProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateEmployeeProfile
import com.asl_emp_mng.app.screens.settings.dataClass.UpdateEmployeeProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.VerifyGSTNumberResponse
import com.asl_emp_mng.app.screens.settings.dataClass.VerifyRegisterNumberResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ViewBranchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
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
        @Body addBranchRequest: AddBranchRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<AddBranchResponse>


    @POST("api/employees-create")
    suspend fun callAddEmp(
        @Body addEmpRequest: AddEmpRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<AddEmpResponse>

    @POST("api/shifts")
    suspend fun callCreateShift(
        @Body request: ShiftCreateRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ShiftCreateResponse>

    @GET("api/shifts")
    suspend fun callShiftList(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ShiftListResponse>

    @POST("api/employees-list")
    suspend fun callEmployeeList(
        @Body request: GetAttendanceRecordRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<EmployeeListResponse>

    @GET("api/branch/list")
    suspend fun callBranchViewList(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ViewBranchResponse>

    @GET("api/departments")
    suspend fun callDepartmentList(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DepartmentResponse>

    @GET("api/branch/list")
    suspend fun callBranchList(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<BranchListResponse>

    @POST("api/jobtitle-list")
    suspend fun callJobTitleList(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<JobTitleResponse>


    @POST("api/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<CompanyRegistrationResponse>

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
    suspend fun callCreateHoliday(
        @Body request: CreateHolidayRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<CreateHolidayResponse>

    @POST("api/company-dashboard")
    suspend fun callCompanySummary(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<AttendanceSummaryResponse>

    @POST("api/leave-request")
    suspend fun callEmployeeLeaveRequest(
        @Body request: EmployeeLeaveRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<EmployeeLeaveResponse>

    @GET("api/company/profile")
    suspend fun callCompanyProfile(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<CompanyProfileResponse>

    @GET("api/holidays")
    suspend fun callHolidayList(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<HolidayListResponse>

    @POST("api/pending-leave-request")
    suspend fun callPendingLeaveRequestList(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<PendingLeaveResponse>


    @POST("api/leave-request-status-change")
    suspend fun callAcceptLeave(
        @Body request: ApproveLeaveRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ApproveLeaveResponse>


    @POST("api/employeesOnLeave")
    suspend fun callOnLeaveList(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<OnLeaveResponse>

    @POST("api/employees-list")
    suspend fun callAllEmpList(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<GetAllEmployeeResponse>


    @POST("api/employees-list")
    suspend fun callEmpRecord(
        @Body request: GetEmpAttendanceRecordBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<GetEmpAttendanceRecord>

    @POST("api/company/update")
    suspend fun callUpdateCompany(
        @Body request: UpdateCompanyProfile,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<UpdateCompanyProfileResponse>

    @GET("api/employee-details/{id}")
    suspend fun callFetchEmployeeDetails(
        @Path("id") id: String,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<FetchEmployeeDetails>

    @POST("api/employees-update/{id}")
    suspend fun callUpdateEmployee(
        @Path("id") id: String,
        @Body request: UpdateEmployeeProfile,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<UpdateEmployeeProfileResponse>


    @POST("api/employee/punch")
    suspend fun callPunchIn(
        @Body request: PunchInRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PunchInResponse>


    @POST("api/employees/assign-shift")
    suspend fun callAssignShift(
        @Body request: AssignShiftRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ShiftAssignmentResponse>

    @POST("api/leave-list")
    suspend fun callAllLeaveList(
        @Body request: LeaveRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<LeaveResponse>

    @POST("api/leave-list")
    suspend fun callGetEmpLeaveList(
        @Body request: GetEmployeeLeaveHistRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<GetEmployeeLeaveHistResponse>

    @POST("api/update-geo-status")
    suspend fun requestGeoLocation(
        @Body request: HashMap<String, String>,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<RequestGeoLocationResponse>

    @POST("api/employee-dashboard")
    suspend fun callEmployeeDashboard(@HeaderMap headers: Map<String, String> = ApiClient.headerMap()): Response<EmployeeDashboardResponse>

    @GET("api/attendance-list")
    suspend fun callMonthlyAttendance(
        @Query("employee_id") employee_id: String,
        @Query("month") month: String,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap(),
    ): Response<MonthAttendaceResponse>

    @POST("api/store-geo-location")
    suspend fun callPostGeoLocation(
        @Body request: EmployeePostLocationRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap(),
    ): Response<EmployeePostLocationResponse>


    @POST("api/get-geo-location")
    suspend fun callGeoLocationHist(
        @Body request: GeoLocationHistResquest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap(),
    ): Response<GeoLocationHistResponse>

    @Multipart
    @POST("api/upload-document")
    suspend fun callCompanyUpdateDocument(
        @PartMap documentTypeIds: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part documents: List<MultipartBody.Part>,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<CompanyUpdateDocumentResponse>

    @GET("api/policy")
    suspend fun callFetchPolicy(
        @Query("company_id") companyId: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap(),
    ): Response<PolicyFetchResponse>


    @Multipart
    @POST("api/policy-create")
    suspend fun createPolicy(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PolicyCreateResponse>


    @Multipart
    @POST("api/employee-documents/create")
    suspend fun callEmployeeUploadDocument(
        @Part("employee_id") employeeId: RequestBody,
        @Part documents: List<MultipartBody.Part>, // Must match Postman structure
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<EmployeeDocumentUploadResponse>

    @POST("api/departments")
    suspend fun callCreateDepartment(
        @Body request: DepartmentCreateRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DepartmentCreateResponse>

    @Multipart
    @POST("api/employees-update/{id}")
    suspend fun updateEmployeeImage(
        @Path("id") employeeId: Int,
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<EmployeeUploadImageResponse>

    @POST("api/document-verify")
    suspend fun callVerifyPan(
        @Body request: PanVerifyRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PanVerifyResponse>


    @POST("api/document-verify")
    suspend fun callVerifyAadhaar(
        @Body request: PanVerifyRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PanVerifyResponse>


    @POST("api/document-verify")
    suspend fun callVerifyCompany(
        @Body request: PanVerifyRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<VerifyRegisterNumberResponse>

    @POST("api/document-verify")
    suspend fun callVerifyGst(
        @Body request: PanVerifyRequestBody,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<VerifyGSTNumberResponse>

    @Multipart
    @POST("api/company/update")
    suspend fun updateCompanyImage(
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<UpdateCompanyProfileResponse>

    @DELETE("api/branch/delete/{id}")
    suspend fun callDeleteBranch(
        @Path("id") id: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteResponse>



    @DELETE("api/shifts/{id}")
    suspend fun callDeleteShift(
        @Path("id") id: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteResponse>

    @DELETE("api/holidays-delete/{id}")
    suspend fun callDeleteHoliday(
        @Path("id") id: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteResponse>
    @POST("api/employee-documents/list")
    suspend fun callEmployeeViewDocument(
        @Body request: EmployeeViewDocumentRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<EmployeeViewDocumentResponse>

    @POST("api/change-device")
    suspend fun callRequestDevice(
        @Body request: ChangeDeviceRequest
    ): Response<ChangeDeviceResponse>


    @POST("api/company/approve-device")
    suspend fun callCompanyAcceptRequestDevice(
        @Body request: CompanyAcceptDeviceRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ChangeDeviceResponse>


    @POST("api/device-requests-list")
    suspend fun callGetCompanyRequestDevice(
        @Body request: CompanyViewRequestDevice,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<CompanyViewRequestDeviceResponse>



    @Multipart
    @POST("api/employee/selfie-attendance")
    suspend fun selfieAttendanceEmp(
        @Part("employee_id") employeeId: RequestBody,
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SelfieAttendanceResponse>

    @POST("api/employee/punch-list")
    suspend fun dayAttendanceRecordEmp(
        @Body request: DayPunchINRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DayPunchINResponse>

    @POST("api/set-attendance-type")
    suspend fun setAttendanceType(
        @Body request: SetAttendanceTypeRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SetAttendanceTypeResponse>

    @POST("api/employee/qr-attendance")
    suspend fun markAttendanceQR(
        @Body request: QRAttendanceMarkRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<QRAttendanceMarkResponse>

}
