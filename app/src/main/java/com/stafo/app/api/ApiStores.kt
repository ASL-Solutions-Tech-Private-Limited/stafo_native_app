package com.stafo.app.api

import com.stafo.app.base.model.EmployeeDashboardResponse
import com.stafo.app.base.model.MonthAttendaceResponse
import com.stafo.app.base.model.RequestGeoLocationResponse
import com.stafo.app.base.request.AddBranchRequest
import com.stafo.app.base.request.OtpRequestBody
import com.stafo.app.base.request.RegisterRequest
import com.stafo.app.base.request.VerifyOtpRequestBody
import com.stafo.app.screens.auth.dataClass.AddBranchResponse
import com.stafo.app.screens.auth.dataClass.BusinessTypeResponse
import com.stafo.app.screens.auth.dataClass.CitiesListResponse
import com.stafo.app.screens.auth.dataClass.CompanyRegistrationResponse
import com.stafo.app.screens.auth.dataClass.CompanyTypeResponse
import com.stafo.app.screens.auth.dataClass.CountryListResponse
import com.stafo.app.screens.auth.dataClass.LoginResponse
import com.stafo.app.screens.auth.dataClass.OtpResponse
import com.stafo.app.screens.auth.dataClass.OtpVerifyResponse
import com.stafo.app.screens.auth.dataClass.SelfieAttendanceResponse
import com.stafo.app.screens.auth.dataClass.StatesListResponse
import com.stafo.app.screens.billpayment.dataClass.BbpsOperatorDetailsResponse
import com.stafo.app.screens.billpayment.dataClass.CategoryMenuResponse
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorRequest
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorResponse
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.chat.dataClass.ChatResponse
import com.stafo.app.screens.chat.dataClass.SendChatRequest
import com.stafo.app.screens.chat.dataClass.SendChatResponse
import com.stafo.app.screens.payroll.dataClass.AllReportsListResponse
import com.stafo.app.screens.payroll.dataClass.SalaryRequest
import com.stafo.app.screens.payroll.dataClass.SalaryResponse
import com.stafo.app.screens.payroll.dataClass.SalarySlipRequest
import com.stafo.app.screens.payroll.dataClass.SalarySlipResponse
import com.stafo.app.screens.performance.dataClass.AddPerformanceRequest
import com.stafo.app.screens.performance.dataClass.AddPerformanceResponse
import com.stafo.app.screens.performance.dataClass.DeletePerformanceRequest
import com.stafo.app.screens.performance.dataClass.DeletePerformanceResponse
import com.stafo.app.screens.performance.dataClass.PerformanceAddRequest
import com.stafo.app.screens.performance.dataClass.PerformanceAddResponse
import com.stafo.app.screens.performance.dataClass.PerformanceTypeResponse
import com.stafo.app.screens.rank.dataClass.PointsRequest
import com.stafo.app.screens.rank.dataClass.PointsResponse
import com.stafo.app.screens.rank.dataClass.RankListRequest
import com.stafo.app.screens.rank.dataClass.RankListResponse
import com.stafo.app.screens.referral.dataClass.ReferralResponse
import com.stafo.app.screens.settings.dataClass.AddEmpRequestBody
import com.stafo.app.screens.settings.dataClass.AddEmpResponse
import com.stafo.app.screens.settings.dataClass.ApproveLeaveRequest
import com.stafo.app.screens.settings.dataClass.ApproveLeaveResponse
import com.stafo.app.screens.settings.dataClass.AssignBranchRequest
import com.stafo.app.screens.settings.dataClass.AssignBranchResponse
import com.stafo.app.screens.settings.dataClass.AssignDepartmentRequest
import com.stafo.app.screens.settings.dataClass.AssignDepartmentResponse
import com.stafo.app.screens.settings.dataClass.AssignShiftRequest
import com.stafo.app.screens.settings.dataClass.AttendanceSummaryResponse
import com.stafo.app.screens.settings.dataClass.AutosuggestResponse
import com.stafo.app.screens.settings.dataClass.BannerResponse
import com.stafo.app.screens.settings.dataClass.BranchListResponse
import com.stafo.app.screens.settings.dataClass.ChangeDeviceRequest
import com.stafo.app.screens.settings.dataClass.ChangeDeviceResponse
import com.stafo.app.screens.settings.dataClass.CompanyAcceptDeviceRequest
import com.stafo.app.screens.settings.dataClass.CompanyProfileResponse
import com.stafo.app.screens.settings.dataClass.CompanyUpdateDocumentResponse
import com.stafo.app.screens.settings.dataClass.CompanyViewRequestDevice
import com.stafo.app.screens.settings.dataClass.CompanyViewRequestDeviceResponse
import com.stafo.app.screens.settings.dataClass.CreateHolidayRequest
import com.stafo.app.screens.settings.dataClass.CreateHolidayResponse
import com.stafo.app.screens.settings.dataClass.CreateLeavePolicyRequest
import com.stafo.app.screens.settings.dataClass.CreateLeavePolicyResponse
import com.stafo.app.screens.settings.dataClass.DayPunchINRequest
import com.stafo.app.screens.settings.dataClass.DayPunchINResponse
import com.stafo.app.screens.settings.dataClass.DeleteCompanyResponse
import com.stafo.app.screens.settings.dataClass.DeleteResponse
import com.stafo.app.screens.settings.dataClass.DepartmentCreateRequest
import com.stafo.app.screens.settings.dataClass.DepartmentCreateResponse
import com.stafo.app.screens.settings.dataClass.DepartmentResponse
import com.stafo.app.screens.settings.dataClass.EmployeeDocumentUploadResponse
import com.stafo.app.screens.settings.dataClass.EmployeeLeaveRequestBody
import com.stafo.app.screens.settings.dataClass.EmployeeLeaveResponse
import com.stafo.app.screens.settings.dataClass.EmployeeListResponse
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationResponse
import com.stafo.app.screens.settings.dataClass.EmployeeUploadImageResponse
import com.stafo.app.screens.settings.dataClass.EmployeeViewDocumentRequest
import com.stafo.app.screens.settings.dataClass.EmployeeViewDocumentResponse
import com.stafo.app.screens.settings.dataClass.FetchEmployeeDetails
import com.stafo.app.screens.settings.dataClass.GenerateQCodeRequest
import com.stafo.app.screens.settings.dataClass.GeoLocationHistResponse
import com.stafo.app.screens.settings.dataClass.GeoLocationHistResquest
import com.stafo.app.screens.settings.dataClass.GetAllEmployeeResponse
import com.stafo.app.screens.settings.dataClass.GetAttendanceBranch
import com.stafo.app.screens.settings.dataClass.GetAttendanceBranchRequest
import com.stafo.app.screens.settings.dataClass.GetAttendanceRecordRequest
import com.stafo.app.screens.settings.dataClass.GetEmpAttendanceRecord
import com.stafo.app.screens.settings.dataClass.GetEmpAttendanceRecordBody
import com.stafo.app.screens.settings.dataClass.GetEmployeeLeaveHistRequestBody
import com.stafo.app.screens.settings.dataClass.GetEmployeeLeaveHistResponse
import com.stafo.app.screens.settings.dataClass.HolidayListResponse
import com.stafo.app.screens.settings.dataClass.InActiveEmpRequest
import com.stafo.app.screens.settings.dataClass.InActiveEmpResponse
import com.stafo.app.screens.settings.dataClass.JobTitleResponse
import com.stafo.app.screens.settings.dataClass.LeaveRequestBody
import com.stafo.app.screens.settings.dataClass.LeaveResponse
import com.stafo.app.screens.settings.dataClass.OnLeaveResponse
import com.stafo.app.screens.settings.dataClass.PanVerifyRequestBody
import com.stafo.app.screens.settings.dataClass.PanVerifyResponse
import com.stafo.app.screens.settings.dataClass.PendingLeaveResponse
import com.stafo.app.screens.settings.dataClass.PolicyCreateResponse
import com.stafo.app.screens.settings.dataClass.PolicyFetchResponse
import com.stafo.app.screens.settings.dataClass.PunchInRequest
import com.stafo.app.screens.settings.dataClass.PunchInResponse
import com.stafo.app.screens.settings.dataClass.QRAttendanceMarkRequest
import com.stafo.app.screens.settings.dataClass.QRAttendanceMarkResponse
import com.stafo.app.screens.settings.dataClass.RemoveSelfieRequest
import com.stafo.app.screens.settings.dataClass.RemoveSelfieResponse
import com.stafo.app.screens.settings.dataClass.ReportsEmployeeListRequest
import com.stafo.app.screens.settings.dataClass.ReportsEmployeeListResponse
import com.stafo.app.screens.settings.dataClass.SalaryGeneratedRequest
import com.stafo.app.screens.settings.dataClass.SalaryGeneratedResponse
import com.stafo.app.screens.settings.dataClass.SalaryTypeDeleteRequest
import com.stafo.app.screens.settings.dataClass.SalaryTypeDeleteResponse
import com.stafo.app.screens.settings.dataClass.SalaryTypeListRequest
import com.stafo.app.screens.settings.dataClass.SalaryTypeListResponse
import com.stafo.app.screens.settings.dataClass.SalaryTypeRequest
import com.stafo.app.screens.settings.dataClass.SalaryTypeResponse
import com.stafo.app.screens.settings.dataClass.SelfieUploadResponse
import com.stafo.app.screens.settings.dataClass.SendFeedbackRequest
import com.stafo.app.screens.settings.dataClass.SendFeedbackResponse
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeRequest
import com.stafo.app.screens.settings.dataClass.SetAttendanceTypeResponse
import com.stafo.app.screens.settings.dataClass.ShiftAssignmentResponse
import com.stafo.app.screens.settings.dataClass.ShiftCreateRequest
import com.stafo.app.screens.settings.dataClass.ShiftCreateResponse
import com.stafo.app.screens.settings.dataClass.ShiftListResponse
import com.stafo.app.screens.settings.dataClass.UpdateCompanyProfile
import com.stafo.app.screens.settings.dataClass.UpdateCompanyProfileResponse
import com.stafo.app.screens.settings.dataClass.UpdateEmployeeProfile
import com.stafo.app.screens.settings.dataClass.UpdateEmployeeProfileResponse
import com.stafo.app.screens.settings.dataClass.UpgradePackageResponse
import com.stafo.app.screens.settings.dataClass.VerifyGSTNumberResponse
import com.stafo.app.screens.settings.dataClass.VerifyRegisterNumberResponse
import com.stafo.app.screens.settings.dataClass.ViewBranchResponse
import com.stafo.app.screens.subscription.dataClass.HashGenerateRequest
import com.stafo.app.screens.subscription.dataClass.HashGenerateResponse
import com.stafo.app.screens.subscription.dataClass.PackageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
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
        @Query("email") email: String, @Query("password") password: String
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
        @Path("id") id: String, @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
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


    @FormUrlEncoded
    @POST("api/employees/assign-shift")
    suspend fun callAssignShift(
        @Field("employee_id") employeeId: String,
        @FieldMap shiftIds: Map<String, String>,
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
        @Path("id") id: Int, @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteResponse>


    @DELETE("api/shifts/{id}")
    suspend fun callDeleteShift(
        @Path("id") id: Int, @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteResponse>

    @DELETE("api/holidays-delete/{id}")
    suspend fun callDeleteHoliday(
        @Path("id") id: Int, @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
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

    @POST("api/generate-qrcode")
    suspend fun generateQR(
        @Body request: GenerateQCodeRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ResponseBody>

    @POST("api/feedback")
    suspend fun sendFeedback(
        @Body request: SendFeedbackRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SendFeedbackResponse>

    @POST("api/company/delete")
    suspend fun deleteAccount(
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeleteCompanyResponse>


    @POST("api/leave-policy")
    suspend fun callCreateLeavePolicy(
        @Body request: CreateLeavePolicyRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<CreateLeavePolicyResponse>


    @POST("api/employees-status-change")
    suspend fun callActiveInactiveEmp(
        @Body request: InActiveEmpRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<InActiveEmpResponse>

    @Multipart
    @POST("api/employee/selfie-image-upload")
    suspend fun uploadSelfieImage(
        @Part("employee_id") employeeId: RequestBody,
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SelfieUploadResponse>

    @POST("api/employee/selfie-image-remove")
    suspend fun callRemoveSelfie(
        @Body request: RemoveSelfieRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<RemoveSelfieResponse>

    @POST("api/upgradeInterested")
    suspend fun callUpgradePackage(
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<UpgradePackageResponse>

    @GET("api/app-banner")
    suspend fun callBannerImage(
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<BannerResponse>

    @POST("api/employee-branch-info")
    suspend fun callAttendanceBranch(
        @Body request: GetAttendanceBranchRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<GetAttendanceBranch>

    @POST("api/employee/assign-branch")
    suspend fun callAssignBranch(
        @Body request: AssignBranchRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<AssignBranchResponse>

    @POST("api/employee/assign-department")
    suspend fun callAssignDepartment(
        @Body request: AssignDepartmentRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<AssignDepartmentResponse>


    @POST("api/report/export-employee")
    suspend fun callReportsEmployeeList(
        @Body request: ReportsEmployeeListRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ReportsEmployeeListResponse>

    @POST("api/report/export-leave")
    suspend fun callReportsLeave(
        @Body request: ReportsEmployeeListRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ReportsEmployeeListResponse>

    @POST("api/report/export-attendace")
    suspend fun callReportsAttendance(
        @Body request: ReportsEmployeeListRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ReportsEmployeeListResponse>

    @POST("api/salarytype/store")
    suspend fun callSalaryType(
        @Body request: SalaryTypeRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalaryTypeResponse>


    @POST("api/salarytype/list")
    suspend fun callSalaryTypeList(
        @Body request: SalaryTypeListRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalaryTypeListResponse>

    @POST("api/salarytype/delete")
    suspend fun callDeleteSalaryType(
        @Body request: SalaryTypeDeleteRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalaryTypeDeleteResponse>

    @POST("api/salary/preview")
    suspend fun callSalaryGenerate(
        @Body request: SalaryGeneratedRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalaryGeneratedResponse>

    @POST("api/salary/save")
    suspend fun callSaveSalary(
        @Body request: SalaryRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalaryResponse>


    @POST("api/employee/salary-slip/download")
    suspend fun callSalarySlip(
        @Body request: SalarySlipRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SalarySlipResponse>

    @GET("api/attendace/reports/list")
    suspend fun callViewReportsList(
        @Query("company_id") companyId: Int
    ): Response<AllReportsListResponse>

    @POST("api/bbps-operators/list")
    suspend fun callElectricityOperator(
        @Body request: ElectricityOperatorRequest
    ): Response<ElectricityOperatorResponse>

    @GET("api/bbps/categories")
    suspend fun callCategoryMenu(
    ): Response<CategoryMenuResponse>


    @POST("api/chat/get")
    suspend fun callChatAdmin(
        @Body request: ChatRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ChatResponse>

    @POST("api/chat/send")
    suspend fun callSendChat(
        @Body request: SendChatRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<SendChatResponse>

    @POST("api/performancetype/list")
    suspend fun callPerformanceType(
        @Body request: ChatRequest, @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PerformanceTypeResponse>


    @POST("api/performancetype/add")
    suspend fun callAddPerformance(
        @Body request: AddPerformanceRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<AddPerformanceResponse>





    @DELETE("api/performancetype/delete/{id}")
    suspend fun callDeletePerformance(
        @Path("id") id: Int,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeletePerformanceResponse>

    @POST("api/performancetype/update/{id}")
    suspend fun callUpdatePerformance(
        @Path("id") id: Int,
        @Body request: AddPerformanceRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<DeletePerformanceResponse>

    @POST("api/performance/save")
    suspend fun callSaveEmpPerformance(
        @Body request: PerformanceAddRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PerformanceAddResponse>



    @POST("api/performance/rank-list")
    suspend fun callRankListEmp(
        @Body request: RankListRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<RankListResponse>

    @POST("api/performance/rank-details")
    suspend fun callPointsDetail(
        @Body request: PointsRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PointsResponse>


    @GET("api/bbps-operators/details/{operator_code}")
    suspend fun callOperatorDetails(
        @Path("operator_code") id: String,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ResponseBody>

    @POST("api/referral-list")
    suspend fun callReferList(
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<ReferralResponse>


    @GET("api/package")
    suspend fun callPackage(
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<PackageResponse>

    @POST("api/hasgenerate")
    suspend fun callHashGenerate(
        @Body request: HashGenerateRequest,
        @HeaderMap headers: Map<String, String> = ApiClient.headerMap()
    ): Response<HashGenerateResponse>


}
