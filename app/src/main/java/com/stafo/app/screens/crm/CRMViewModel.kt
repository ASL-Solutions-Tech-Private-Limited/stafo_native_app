package com.stafo.app.screens.crm

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.stafo.app.ASLEmpMng
import com.stafo.app.R
import com.stafo.app.base.BaseViewModel
import com.stafo.app.base.model.ErrorResponse
import com.stafo.app.screens.auth.LoginActivity
import com.stafo.app.screens.crm.dataClass.CRMDashboardResponse
import com.stafo.app.screens.crm.dataClass.CreateFollowUpRequest
import com.stafo.app.screens.crm.dataClass.CreateFollowUpResponse
import com.stafo.app.screens.crm.dataClass.FollowUpListResponse
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.screens.crm.dataClass.LeadCreateResponse
import com.stafo.app.screens.crm.dataClass.LeadDashboardRequest
import com.stafo.app.screens.crm.dataClass.LeadListRequest
import com.stafo.app.screens.crm.dataClass.LeadListResponse
import com.stafo.app.screens.crm.dataClass.UpdateLeadResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CRMViewModel: BaseViewModel() {
    private var mCRMDashboard: MutableLiveData<CRMDashboardResponse> = MutableLiveData()

    val mCRMDashboardResponse: LiveData<CRMDashboardResponse> get() = mCRMDashboard


    private var mLeadList: MutableLiveData<LeadListResponse> = MutableLiveData()

    val mLeadListResponse: LiveData<LeadListResponse> get() = mLeadList



    private var mLeadCreate: MutableLiveData<LeadCreateResponse> = MutableLiveData()

    val mLeadCreateResponse: LiveData<LeadCreateResponse> get() = mLeadCreate


    private var mCreateFollowUp: MutableLiveData<CreateFollowUpResponse> = MutableLiveData()

    val mCreateFollowUpResponse: LiveData<CreateFollowUpResponse> get() = mCreateFollowUp


    private var mFollowUpList: MutableLiveData<FollowUpListResponse> = MutableLiveData()

    val mFollowUpListResponse: LiveData<FollowUpListResponse> get() = mFollowUpList


    private var mUpdateLead: MutableLiveData<UpdateLeadResponse> = MutableLiveData()



    val mUpdateLeadResponse: LiveData<UpdateLeadResponse> get() = mUpdateLead

    fun updateLead(mContext: Context, id: Int,request:LeadCreateRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callUpdateLead(id,request)
                Log.d("crm", "lead update: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mUpdateLead.postValue(it.body())
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



    fun getFollowUpList(mContext: Context, id: String) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callFollowUpList(id)
                Log.d("crm", "follow-up list: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mFollowUpList.postValue(it.body())
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


    fun createNewFollowUp(mContext: Context, request: CreateFollowUpRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callCreateFollowUp(request)
                Log.d("crm", "follow-up create: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCreateFollowUp.postValue(it.body())
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


    fun createNewLead(mContext: Context, request: LeadCreateRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callCreateLead(request)
                Log.d("crm", "lead create: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mLeadCreate.postValue(it.body())
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


    fun getAllLeadList(mContext: Context, isCompanyLogin:Boolean,id:String) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callLeadList(
                    employeeId = if (isCompanyLogin) null else id,
                    companyId = if (isCompanyLogin) id else null
                )
                Log.d("crm", "lead list: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mLeadList.postValue(it.body())
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

    fun getLeadDashboard(mContext: Context,isCompanyLogin:Boolean,id:String) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCRMDashboard(
                    employeeId = if (isCompanyLogin) null else id,
                    companyId = if (isCompanyLogin) id else null
                )
                Log.d("crm", "crm dashboard: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCRMDashboard.postValue(it.body())
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
                    CustomToast(mContext,"hhf "+mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


}