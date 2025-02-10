package com.asl_emp_mng.app.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.asl_emp_mng.app.base.BaseViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.asl_emp_mng.app.ASLEmpMng
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.ErrorResponse
import com.asl_emp_mng.app.base.model.OwnerInfo
import com.asl_emp_mng.app.base.request.AddBranchRequest
import com.asl_emp_mng.app.base.request.RegisterRequest
import com.asl_emp_mng.app.screens.auth.dataClass.BusinessTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CitiesListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CountryListResponse
import com.asl_emp_mng.app.screens.auth.dataClass.StatesListResponse
import com.asl_emp_mng.app.screens.dashboard.EmployerDashboard
import com.asl_emp_mng.app.utils.CustomToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel() : BaseViewModel() {


    private var mCompanyType: MutableLiveData<CompanyTypeResponse> = MutableLiveData()
    val mCompanyTypeResponse: LiveData<CompanyTypeResponse> get() = mCompanyType


    private var mCountry: MutableLiveData<CountryListResponse> = MutableLiveData()
    val mCountryResponse: LiveData<CountryListResponse> get() = mCountry

    private var mState: MutableLiveData<StatesListResponse> = MutableLiveData()
    val mStateResponse: LiveData<StatesListResponse> get() = mState

    private var mCity: MutableLiveData<CitiesListResponse> = MutableLiveData()
    val mCityResponse: LiveData<CitiesListResponse> get() = mCity

    private var mBusinessType: MutableLiveData<BusinessTypeResponse> = MutableLiveData()
    val mBusinessTypeResponse: LiveData<BusinessTypeResponse> get() = mBusinessType

    fun getCompanyType(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCompanyType()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCompanyType.postValue(it.body())
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

    fun createBranch(mContext: Context,latitude:String,longitude:String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val addBranchRequest = AddBranchRequest(
                    company_id = 1,
                    branch_name = "Cafe",
                    branch_address = "Kolkata",
                    latitute = latitude,
                    longtitute = longitude,
                    radar = "200"
                )
                val response = ASLEmpMng.instance.apiStores()?.callCreateBranch(addBranchRequest)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                           Log.d("res",it.message())
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

    fun registerUser(mContext: Context, companyInfo: CompanyInfo, ownerInfo: OwnerInfo) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = RegisterRequest(company_info = companyInfo, owner_info = ownerInfo)

                val response = ASLEmpMng.instance.apiStores()?.registerUser(request)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response != null) {
                        if (response.isSuccessful) {
                            Log.d("API_SUCCESS", "Response: ${response.body()}")
                            mContext.startActivity(Intent(mContext, EmployerDashboard::class.java))
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("API_ERROR", "Error Body: $errorBody")

                            errorBody?.let { errorJson ->
                                val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "Unknown error")
                            } ?: run {
                                CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                            }
                        }
                    } else {
                        Log.e("API_ERROR", "Response is null")
                        CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun userLogin(mContext: Context, email: String, password: String) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API_SUCCESS", "post  login : $email $password")
                val response = ASLEmpMng.instance.apiStores()?.userLogin(email = email, password = password)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response != null) {
                        if (response.isSuccessful) {
                            Log.d("API_SUCCESS", "Response login : ${response.body()}")
                            mContext.startActivity(Intent(mContext, EmployerDashboard::class.java))
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("API_ERROR", "Error Body: $errorBody")

                            errorBody?.let { errorJson ->
                                val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "Unknown error")
                            } ?: run {
                                CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                            }
                        }
                    } else {
                        Log.e("API_ERROR", "Response is null")
                        CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }



    fun getBusinessType(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callBusinessType()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mBusinessType.postValue(it.body())
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

    fun getCountryList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCountryList()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCountry.postValue(it.body())
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

    fun getStateList(mContext: Context, countryId: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callStatesList(countryId)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mState.postValue(it.body())
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

    fun getCityList(mContext: Context, stateId: String) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCityList(stateId)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCity.postValue(it.body())
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