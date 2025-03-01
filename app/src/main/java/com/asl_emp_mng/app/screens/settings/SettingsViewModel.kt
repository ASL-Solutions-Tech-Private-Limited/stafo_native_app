package com.asl_emp_mng.app.screens.settings

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asl_emp_mng.app.ASLEmpMng
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseViewModel
import com.asl_emp_mng.app.base.model.EmployeeDashboardResponse
import com.asl_emp_mng.app.base.model.ErrorResponse
import com.asl_emp_mng.app.base.model.MonthAttendaceResponse
import com.asl_emp_mng.app.base.model.RequestGeoLocationResponse
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.screens.auth.LoginActivity
import com.asl_emp_mng.app.screens.auth.dataClass.AddBranchResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AssignShiftRequest
import com.asl_emp_mng.app.screens.settings.dataClass.AttendanceSummaryResponse
import com.asl_emp_mng.app.screens.settings.dataClass.BranchListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyUpdateDocumentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayResponse
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
import com.asl_emp_mng.app.utils.CustomToast
import com.asl_emp_mng.app.utils.getUserAccessToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class SettingsViewModel : BaseViewModel() {

    private var mEmployeeGeoLocation: MutableLiveData<EmployeePostLocationResponse> =
        MutableLiveData()

    val mEmployeeGeoLocationResponse: LiveData<EmployeePostLocationResponse> get() = mEmployeeGeoLocation

    fun postGeoLocation(mContext: Context, request: EmployeePostLocationRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callPostGeoLocation(request)
                Log.d("res", "Location: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeGeoLocation.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    private var mCreateBranch: MutableLiveData<AddBranchResponse> = MutableLiveData()

    val mCreateBranchResponse: LiveData<AddBranchResponse> get() = mCreateBranch

    private var mAddEmp: MutableLiveData<AddEmpResponse> = MutableLiveData()
    val mAddEmpResponse: LiveData<AddEmpResponse> get() = mAddEmp

    private var mDepartment: MutableLiveData<DepartmentResponse> = MutableLiveData()
    val mDepartmentListResponse: LiveData<DepartmentResponse> get() = mDepartment

    private var mBranch: MutableLiveData<BranchListResponse> = MutableLiveData()
    val mBranchListResponse: LiveData<BranchListResponse> get() = mBranch

    private var mShiftCreate: MutableLiveData<ShiftCreateResponse> = MutableLiveData()
    val mShiftCreateResponse: LiveData<ShiftCreateResponse> get() = mShiftCreate


    private var mShiftList: MutableLiveData<ShiftListResponse> = MutableLiveData()
    val mShiftListResponse: LiveData<ShiftListResponse> get() = mShiftList


    private var mEmployeeList: MutableLiveData<EmployeeListResponse> = MutableLiveData()
    val mEmployeeListResponse: LiveData<EmployeeListResponse> get() = mEmployeeList

    private var mViewBranchList: MutableLiveData<ViewBranchResponse> = MutableLiveData()

    val mViewBranchResponse: LiveData<ViewBranchResponse> get() = mViewBranchList

    private var mAttendanceSummary: MutableLiveData<AttendanceSummaryResponse> = MutableLiveData()
    val mAttendanceSummaryResponse: LiveData<AttendanceSummaryResponse> get() = mAttendanceSummary


    private var mCreateHoliday: MutableLiveData<CreateHolidayResponse> = MutableLiveData()

    val mCreateHolidayResponse: LiveData<CreateHolidayResponse> get() = mCreateHoliday


    private var mEmpLeaveRequest: MutableLiveData<EmployeeLeaveResponse> = MutableLiveData()
    val mEmpLeaveRequestResponse: LiveData<EmployeeLeaveResponse> get() = mEmpLeaveRequest


    private var mCompanyProfile: MutableLiveData<CompanyProfileResponse> = MutableLiveData()
    val mCompanyProfileResponse: LiveData<CompanyProfileResponse> get() = mCompanyProfile

    private var mHolidayList: MutableLiveData<HolidayListResponse> = MutableLiveData()

    val mHolidayListResponse: LiveData<HolidayListResponse> get() = mHolidayList

    private var mPendingLeaveList: MutableLiveData<PendingLeaveResponse> = MutableLiveData()
    val mPendingLeaveListResponse: LiveData<PendingLeaveResponse> get() = mPendingLeaveList

    private var mApproveLeave: MutableLiveData<ApproveLeaveResponse> = MutableLiveData()
    val mApproveLeaveResponse: LiveData<ApproveLeaveResponse> get() = mApproveLeave


    private var mOnLeave: MutableLiveData<OnLeaveResponse> = MutableLiveData()
    val mOnLeaveResponse: LiveData<OnLeaveResponse> get() = mOnLeave

    private var mGetAllEmployee: MutableLiveData<GetAllEmployeeResponse> = MutableLiveData()
    val mGetAllEmployeeResponse: LiveData<GetAllEmployeeResponse> get() = mGetAllEmployee

    private var mGetEmployeeRecord: MutableLiveData<GetEmpAttendanceRecord> = MutableLiveData()

    val mGetEmployeeRecordResponse: LiveData<GetEmpAttendanceRecord> get() = mGetEmployeeRecord


    private var mUpdateCompany: MutableLiveData<UpdateCompanyProfileResponse> = MutableLiveData()
    val mUpdateCompanyResponse: LiveData<UpdateCompanyProfileResponse> get() = mUpdateCompany

    private var mFetchEmployeeDetails: MutableLiveData<FetchEmployeeDetails> = MutableLiveData()

    val mFetchEmployeeDetailsResponse: LiveData<FetchEmployeeDetails> get() = mFetchEmployeeDetails


    private var mUpdateEmployeeProfile: MutableLiveData<UpdateEmployeeProfileResponse> =
        MutableLiveData()
    val mmUpdateEmployeeProfileResponse: LiveData<UpdateEmployeeProfileResponse> get() = mUpdateEmployeeProfile


    private var mPunchIn: MutableLiveData<PunchInResponse> = MutableLiveData()
    val mPunchInResponse: LiveData<PunchInResponse> get() = mPunchIn

    private var mShiftAssignment: MutableLiveData<ShiftAssignmentResponse> = MutableLiveData()
    val mShiftAssignmentResponse: LiveData<ShiftAssignmentResponse> get() = mShiftAssignment

    private var mLeave: MutableLiveData<LeaveResponse> = MutableLiveData()
    val mLeaveResponse: LiveData<LeaveResponse> get() = mLeave

    private var mGetEmployeeLeaveHist: MutableLiveData<GetEmployeeLeaveHistResponse> =
        MutableLiveData()
    val mGetEmployeeLeaveHistResponse: LiveData<GetEmployeeLeaveHistResponse> get() = mGetEmployeeLeaveHist


    private var mSendGeoLocation: MutableLiveData<RequestGeoLocationResponse> = MutableLiveData()
    val mSendGeoLocationResponse: LiveData<RequestGeoLocationResponse> get() = mSendGeoLocation

    private var mEmployeeDashoard: MutableLiveData<EmployeeDashboardResponse> = MutableLiveData()
    val mEmployeeDashboardResponse: LiveData<EmployeeDashboardResponse> get() = mEmployeeDashoard

    private var mAttendanceHistory: MutableLiveData<MonthAttendaceResponse> = MutableLiveData()
    val mAttendanceHistoryResponse: LiveData<MonthAttendaceResponse> get() = mAttendanceHistory


    private var mJobTitle: MutableLiveData<JobTitleResponse> = MutableLiveData()

    val mJobTitleResponse: LiveData<JobTitleResponse> get() = mJobTitle

    private var mGeoLocationHist: MutableLiveData<GeoLocationHistResponse> = MutableLiveData()

    val mGeoLocationHistResponse: LiveData<GeoLocationHistResponse> get() = mGeoLocationHist

    private var mCompanyUpdateDocument: MutableLiveData<CompanyUpdateDocumentResponse> =
        MutableLiveData()

    val mCompanyUpdateDocumentResponse: LiveData<CompanyUpdateDocumentResponse> get() = mCompanyUpdateDocument


    private var mPolicyFetch: MutableLiveData<PolicyFetchResponse> = MutableLiveData()

    val mPolicyFetchResponse: LiveData<PolicyFetchResponse> get() = mPolicyFetch


    private var mPolicyCreate: MutableLiveData<PolicyCreateResponse> = MutableLiveData()

    val mPolicyCreateResponse: LiveData<PolicyCreateResponse> get() = mPolicyCreate

    private var mEmployeeDocumentUpload: MutableLiveData<EmployeeDocumentUploadResponse> =
        MutableLiveData()

    val mEmployeeDocumentUploadResponse: LiveData<EmployeeDocumentUploadResponse> get() = mEmployeeDocumentUpload


    private var mDepartmentCreate: MutableLiveData<DepartmentCreateResponse> = MutableLiveData()

    val mDepartmentCreateResponse: LiveData<DepartmentCreateResponse> get() = mDepartmentCreate

    private var mEmployeeUploadImage: MutableLiveData<EmployeeUploadImageResponse> = MutableLiveData()

    val mEmployeeUploadImageResponse: LiveData<EmployeeUploadImageResponse> get() = mEmployeeUploadImage


    private var mCompanyUploadImage: MutableLiveData<UpdateCompanyProfileResponse> = MutableLiveData()

    val mCompanyUploadImageResponse: LiveData<UpdateCompanyProfileResponse> get() = mCompanyUploadImage

    private var mPanVerify: MutableLiveData<PanVerifyResponse> = MutableLiveData()

    val mPanVerifyResponse: LiveData<PanVerifyResponse> get() = mPanVerify


    private var mVerifyGSTNumber: MutableLiveData<VerifyGSTNumberResponse> = MutableLiveData()

    val mVerifyGSTNumberResponse: LiveData<VerifyGSTNumberResponse> get() = mVerifyGSTNumber




    private var mVerifyRegisterNumber: MutableLiveData<VerifyRegisterNumberResponse> = MutableLiveData()

    val mVerifyRegisterNumberResponse: LiveData<VerifyRegisterNumberResponse> get() = mVerifyRegisterNumber



    private var mDelete: MutableLiveData<DeleteResponse> = MutableLiveData()

    val mDeleteResponse: LiveData<DeleteResponse> get() = mDelete

    private var mEmployeeViewDocument: MutableLiveData<EmployeeViewDocumentResponse> = MutableLiveData()

    val mEmployeeViewDocumentResponse: LiveData<EmployeeViewDocumentResponse> get() = mEmployeeViewDocument



    fun viewEmployeeDocuments(
        mContext: Context,
        request: EmployeeViewDocumentRequest
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeViewDocument(request)
                Log.d("res", "document :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeViewDocument.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }







    fun companyDeleteBranch(
        mContext: Context,
        id:Int
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteBranch(id)
                Log.d("res", "rgs verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDelete.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }
    fun companyDeleteShift(
        mContext: Context,
        id:Int
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteShift(id)
                Log.d("res", "rgs verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDelete.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun companyDeleteHoliday(
        mContext: Context,
        id:Int
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteHoliday(id)
                Log.d("res", "rgs verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDelete.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }





    fun companyRGSVerify(
        mContext: Context,
        request: PanVerifyRequestBody
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callVerifyCompany(request)
                Log.d("res", "rgs verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mVerifyRegisterNumber.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }






    fun companyGSTVerify(
        mContext: Context,
        request: PanVerifyRequestBody
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callVerifyGst(request)
                Log.d("res", "gst verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mVerifyGSTNumber.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun companyPanVerify(
        mContext: Context,
        request: PanVerifyRequestBody
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callVerifyPan(request)
                Log.d("res", "pan verify c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPanVerify.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }













    fun changeCompanyProfileImage(
        mContext: Context,
        file: File?
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {

                if (file == null) {
                    withContext(Dispatchers.Main) {
                        getLoaderLiveData().postValue("stop")
                        CustomToast(mContext, "File is null, cannot upload image.")
                    }
                    return@launch
                }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                val imagePart = MultipartBody.Part.createFormData("image_name", file.name, requestFile)

                val response = ASLEmpMng.instance.apiStores()?.updateCompanyImage(imagePart)


                Log.d("res", "company change image :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCompanyUploadImage.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }








    fun changeEmpProfileImage(
        mContext: Context,
        employeeId: Int,
        file: File?
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("res", "emp change image call method :")
                if (file == null) {
                    Log.d("res", "emp change image null file :")
                    withContext(Dispatchers.Main) {
                        getLoaderLiveData().postValue("stop")
                        CustomToast(mContext, "File is null, cannot upload image.")
                    }
                    return@launch
                }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = ASLEmpMng.instance.apiStores()?.updateEmployeeImage(
                    employeeId, imagePart)


                Log.d("res", "emp change image :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeUploadImage.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }






    fun createDepartment(
        mContext: Context,
        request: DepartmentCreateRequest
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCreateDepartment(request)
                Log.d("res", "department c :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDepartmentCreate.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun postEmpUploadDocument(
        mContext: Context,
        employeeId: String,
        documents: List<Triple<String, String, File>>
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val employeeIdBody = employeeId.toRequestBody("text/plain".toMediaTypeOrNull())

                val documentParts = mutableListOf<MultipartBody.Part>()

                documents.forEachIndexed { index, (documentName, documentTypeId, file) ->

                    val documentNameBody =
                        documentName.toRequestBody("text/plain".toMediaTypeOrNull())
                    documentParts.add(
                        MultipartBody.Part.createFormData(
                            "documents[$index][document_name]",
                            documentName
                        )
                    )

                    val documentTypeIdBody =
                        documentTypeId.toRequestBody("text/plain".toMediaTypeOrNull())
                    documentParts.add(
                        MultipartBody.Part.createFormData(
                            "documents[$index][document_type_id]",
                            documentTypeId
                        )
                    )

                    val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                    documentParts.add(
                        MultipartBody.Part.createFormData(
                            "documents[$index][file]",
                            file.name,
                            requestFile
                        )
                    )
                }

                val response = ASLEmpMng.instance.apiStores()?.callEmployeeUploadDocument(
                    employeeId = employeeIdBody,
                    documents = documentParts
                )
                Log.d("res", "emp doc :${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeDocumentUpload.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun uploadPolicy(mContext: Context, title: String, description: String, file: File) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
                val descPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)

                val requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response =
                    ASLEmpMng.instance.apiStores()?.createPolicy(titlePart, descPart, filePart)

                Log.d("res", "policy create " + response?.body().toString())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPolicyCreate.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun fetchPolicy(mContext: Context,id:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callFetchPolicy(id)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPolicyFetch.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun postCompanyUpdateDocument(
        mContext: Context,
        imageUris: List<Uri>,
        documentTypeIds: List<Int>
    ) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentTypeParts = mutableMapOf<String, RequestBody>()

                documentTypeIds.forEachIndexed { index, id ->
                    documentTypeParts["document_type_id[$index]"] =
                        id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }


                val documentParts = imageUris.mapIndexedNotNull { index, uri ->
                    val file = getFileFromUri(mContext, uri) ?: return@mapIndexedNotNull null
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("document[$index]", file.name, requestFile)
                }


                if (documentParts.isEmpty()) {
                    Log.e("UploadError", "No valid files to upload.")
                    withContext(Dispatchers.Main) {
                        getLoaderLiveData().value = "stop"
                        CustomToast(mContext, "No valid files found.")
                    }
                    return@launch
                }


                val response = ASLEmpMng.instance.apiStores()?.callCompanyUpdateDocument(
                    documentTypeIds = documentTypeParts,
                    documents = documentParts
                )




                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCompanyUpdateDocument.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "Error occurred")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, "Something went wrong!")
                }
            }
        }
    }


    fun getFileFromUri(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun getGeoLocationHist(mContext: Context, request: GeoLocationHistResquest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callGeoLocationHist(request)
                Log.d("res", "geo hist " + response?.body().toString())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mGeoLocationHist.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getJobTitleList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callJobTitleList()
                Log.d("res", "job " + response?.body().toString())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mJobTitle.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getEmployeeLeaveHist(mContext: Context, request: GetEmployeeLeaveHistRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callGetEmpLeaveList(request)
                Log.d("res", "Leave Data: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mGetEmployeeLeaveHist.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getAllLeaveList(mContext: Context, request: LeaveRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callAllLeaveList(request)
                Log.d("res", "Leave Data: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mLeave.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun assignShift(mContext: Context, request: AssignShiftRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callAssignShift(request)
                Log.d("res", "mPunchIn: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mShiftAssignment.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun punchInRequest(mContext: Context, request: PunchInRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callPunchIn(request)
                Log.d("res", "mPunchIn: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPunchIn.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun updateEmployeeDetails(mContext: Context, id: String, request: UpdateEmployeeProfile) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                Log.d("res", "update: $id ${getUserAccessToken()}")
                val response = ASLEmpMng.instance.apiStores()?.callUpdateEmployee(id, request)
                Log.d("res", "update: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mUpdateEmployeeProfile.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun fetchEmployeeDetails(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callFetchEmployeeDetails(id)
                Log.d("res", "details: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mFetchEmployeeDetails.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun updateCompanyProfile(mContext: Context, request: UpdateCompanyProfile) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callUpdateCompany(request)
                Log.d("res", "record: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mUpdateCompany.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getEmployeeAttendRecord(mContext: Context, id: String, date: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GetEmpAttendanceRecordBody(id, date)

                val response = ASLEmpMng.instance.apiStores()?.callEmpRecord(request)
                Log.d("res", "record: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mGetEmployeeRecord.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getAllEmployeeList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAllEmpList()
                Log.d("res", "post: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mGetAllEmployee.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getOnLeaveList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callOnLeaveList()
                Log.d("res", "pending: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mOnLeave.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun postPendingLeave(mContext: Context, request: ApproveLeaveRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAcceptLeave(request)
                Log.d("res", "pending: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mApproveLeave.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getPendingLeaveList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callPendingLeaveRequestList()
                Log.d("res", "pending: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPendingLeaveList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getHolidayList(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callHolidayList(id.toInt())
                Log.d("res","holiday: ${response?.body()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mHolidayList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getCompanyDetails(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCompanyProfile()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCompanyProfile.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun requestLeaveEmp(mContext: Context, request: EmployeeLeaveRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeLeaveRequest(request)

                Log.d("res", "leave :${response?.body()}")

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmpLeaveRequest.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun addHoliday(mContext: Context, request: CreateHolidayRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCreateHoliday(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCreateHoliday.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getCompanyDashboard(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCompanySummary()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    response?.let {
                        if (it.isSuccessful) {
                            mAttendanceSummary.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getEmpList(mContext: Context, date: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GetAttendanceRecordRequest(
                    date = date
                )
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeList(request)
                Log.d("res", "get :${response?.body()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getViewBranchList(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                Log.d("res", "post data : ${getUserAccessToken()}")
                val response = ASLEmpMng.instance.apiStores()?.callBranchViewList(id.toInt())

                Log.d("res", "branch: ${response?.body()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mViewBranchList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getShiftList(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callShiftList(id.toInt())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mShiftList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getBranchList(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                Log.d("res", "id  $id")

                val response = ASLEmpMng.instance.apiStores()?.callBranchList(id.toInt())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mBranch.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getDepartmentList(mContext: Context, id: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                Log.d("res", "id  $id")
                val response = ASLEmpMng.instance.apiStores()?.callDepartmentList(id.toInt())
                Log.d("res", "id  $id " + response?.body())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDepartment.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun createBranch(mContext: Context, request: AddBranchRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callCreateBranch(request)
                Log.d("res", "branch  " + response?.body())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCreateBranch.postValue(response.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun addEmployee(mContext: Context, request: AddEmpRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callAddEmp(request)
                Log.d("res", "res first  data ${request.toString()} : ${response?.body()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {

                        if (it.isSuccessful) {

                            mAddEmp.postValue(response.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun createNewShift(mContext: Context, request: ShiftCreateRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCreateShift(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {

                        Log.d("res", "res first  data : $it ${response.body()}")
                        if (it.isSuccessful) {
                            Log.d("res", "res data : $it ${response.body()}")
                            mShiftCreate.postValue(response.body())
                        } else {

                            Log.d("res", "res error data : $it ${response.body()}")
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun sendGeoLocationRequest(mContext: Context, empID: String, permission: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hashMap = HashMap<String, String>()
                hashMap.put("employee_id", empID)
                hashMap.put("geo_status", permission)
                val response = ASLEmpMng.instance.apiStores()?.requestGeoLocation(hashMap)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {

                        Log.d("res", "res first  data : $it ${response.body()}")
                        if (it.isSuccessful) {
                            Log.d("res", "res data : $it ${response.body()}")
                            mSendGeoLocation.postValue(response.body())
                        } else {

                            Log.d("res", "res error data : $it ${response.body()}")
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getEmployeDashboard(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callEmployeeDashboard()
                Log.d("res", "dash " + response?.body().toString())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeDashoard.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getMonthlyAttendance(mContext: Context, date: String, emp: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callMonthlyAttendance(emp, date)
                Log.d("res", "monthly " + response?.body())
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAttendanceHistory.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


}