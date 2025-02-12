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
import com.asl_emp_mng.app.screens.auth.dataClass.CountryListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpRequestBody
import com.asl_emp_mng.app.screens.settings.dataClass.AddEmpResponse
import com.asl_emp_mng.app.screens.settings.dataClass.BranchListResponse
import com.asl_emp_mng.app.screens.settings.dataClass.DepartmentResponse
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeListResponse
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


    fun getEmpList(mContext: Context, token: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callEmployeeList(bearerToken)
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

    fun getViewBranchList(mContext: Context, token: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callBranchViewList(bearerToken)
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


    fun getShiftList(mContext: Context, token: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callShiftList(bearerToken)
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


    fun getBranchList(mContext: Context, token: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callBranchList(bearerToken)
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

    fun getDepartmentList(mContext: Context, token: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callDepartmentList(bearerToken)
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


    fun createBranch(mContext: Context, token: String, latitude: String, longitude: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val bearerToken = "Bearer $token"

                val addBranchRequest = AddBranchRequest(
                    company_id = 1,
                    branch_name = "Cafe 5",
                    branch_address = "Kolkata",
                    latitute = latitude,
                    longtitute = longitude,
                    radar = "200"
                )
                val response =
                    ASLEmpMng.instance.apiStores()?.callCreateBranch(bearerToken, addBranchRequest)
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

    fun addEmployee(mContext: Context, token: String, request: AddEmpRequestBody) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callAddEmp(bearerToken, request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {

                        Log.d("res", "res first  data : $it ${response.body()}")
                        if (it.isSuccessful) {
                            Log.d("res", "res data : $it ${response.body()}")

                            mAddEmp.postValue(response.body())
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

    fun createNewShift(mContext: Context, token: String, request: ShiftCreateRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val bearerToken = "Bearer $token"
                val response = ASLEmpMng.instance.apiStores()?.callCreateShift(bearerToken, request)
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