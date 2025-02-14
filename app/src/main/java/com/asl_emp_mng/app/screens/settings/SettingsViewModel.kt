package com.asl_emp_mng.app.screens.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asl_emp_mng.app.ASLEmpMng
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.BaseViewModel
import com.asl_emp_mng.app.base.model.ErrorResponse
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.screens.auth.LoginActivity
import com.asl_emp_mng.app.screens.auth.dataClass.AddBranchResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ApproveLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AttendanceSummaryResponse
import com.asl_emp_mng.app.screens.settings.dataClass.BranchListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CompanyProfileResponse
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayRequest
import com.asl_emp_mng.app.screens.settings.dataClass.CreateHolidayResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.GetAllEmployeeResponse
import com.asl_emp_mng.app.screens.settings.dataClass.GetAttendanceRecordRequest
import com.asl_emp_mng.app.screens.settings.dataClass.HolidayListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.OnLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.PendingLeaveResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateRequest
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftCreateResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ShiftListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.ViewBranchResponse
import com.asl_emp_mng.app.utils.CustomToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel : BaseViewModel() {


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






    fun getAllEmployeeList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAllEmpList()
                Log.d("res","post: ${response?.body().toString()}")
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
                Log.d("res","pending: ${response?.body().toString()}")
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






    fun postPendingLeave(mContext: Context,request:ApproveLeaveRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAcceptLeave(request)
                Log.d("res","pending: ${response?.body().toString()}")
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
                Log.d("res","pending: ${response?.body().toString()}")
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

    fun getHolidayList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callHolidayList()
                Log.d("res",response?.body().toString())
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



    fun requestLeaveEmp(mContext: Context,request: EmployeeLeaveRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeLeaveRequest(request)
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


    fun addHoliday(mContext: Context,request: CreateHolidayRequest) {
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
                    Log.d("API_",response?.body().toString())

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




    fun getEmpList(mContext: Context,date:String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                 val request=GetAttendanceRecordRequest(
                     date=date
                 )
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeList(request)
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

    fun getViewBranchList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callBranchViewList()
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


    fun getShiftList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callShiftList()
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


    fun getBranchList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callBranchList()
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

    fun getDepartmentList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDepartmentList()
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


    fun createBranch(mContext: Context, latitude: String, longitude: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val addBranchRequest = AddBranchRequest(
                    company_id = 1,
                    branch_name = "Cafe 5",
                    branch_address = "Kolkata",
                    latitute = latitude,
                    longtitute = longitude,
                    radar = "200"
                )
                val response =
                    ASLEmpMng.instance.apiStores()?.callCreateBranch(addBranchRequest)
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

    fun createNewShift(mContext: Context ,request: ShiftCreateRequest) {
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


}