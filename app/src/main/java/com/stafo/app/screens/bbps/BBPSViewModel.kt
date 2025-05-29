package com.stafo.app.screens.bbps

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
import com.stafo.app.screens.bbps.dataClasses.BBPSCategoryResponse
import com.stafo.app.screens.bbps.dataClasses.BillerBillFetchResponse
import com.stafo.app.screens.bbps.dataClasses.BillerDetailsResponse
import com.stafo.app.screens.bbps.dataClasses.BillerListResponse
import com.stafo.app.screens.bbps.dataClasses.InitiateBBPSBillResponse
import com.stafo.app.screens.subscription.dataClass.CheckPaymentStatusRequest
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BBPSViewModel() : BaseViewModel() {


    private var mBBPSCategory: MutableLiveData<BBPSCategoryResponse> = MutableLiveData()
    val mBBPSCategoryResponse: LiveData<BBPSCategoryResponse> get() = mBBPSCategory

    private var mBBPSBillers: MutableLiveData<BillerListResponse> = MutableLiveData()
    val mBBPSBillersResponse: LiveData<BillerListResponse> get() = mBBPSBillers

    private var mBillerDetails: MutableLiveData<BillerDetailsResponse> = MutableLiveData()
    val mBillerDetailsResponse: LiveData<BillerDetailsResponse> get() = mBillerDetails

    private var mBillerBill: MutableLiveData<BillerBillFetchResponse> = MutableLiveData()
    val mBillerBillResponse: LiveData<BillerBillFetchResponse> get() = mBillerBill

    private var mInitiateBillPayment: MutableLiveData<InitiateBBPSBillResponse> = MutableLiveData()
    val mInitiateBillPaymentResponse: LiveData<InitiateBBPSBillResponse> get() = mInitiateBillPayment


    fun getBBPSCategory(mContext: Context) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callBillerCategory()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mBBPSCategory.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getBillersByCategory(mContext: Context, category: String) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = HashMap<String, String>()
                request["category"] = category
                request["circle"] = ""
                val response = ASLEmpMng.instance.apiStores()?.callBillerList(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mBBPSBillers.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun getBillersDetails(mContext: Context, billerOperatorId: String) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = HashMap<String, String>()
                request["operator_code"] = billerOperatorId
                //  request["circle"] = ""
                val response = ASLEmpMng.instance.apiStores()?.callBillerDetails(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mBillerDetails.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun fetchBill(mContext: Context, request: HashMap<String, Any>) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callBillerBill(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mBillerBill.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun initiateBillPayment(mContext: Context, request: HashMap<String, Any>) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callInitiateBillPayment(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mInitiateBillPayment.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun checkPaymentStatus(mContext: Context, request: CheckPaymentStatusRequest) {
        if (!isNetworkAvailable(mContext)) {
            CustomToast(
                mContext,
                "Network not available.Please check your internet connection and try again."
            )
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCheckPaymentStatus(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.d("res", response?.body().toString())
                    if (response != null && response.isSuccessful) {
                        mInitiateBillPayment.postValue(response.body())
                    } else {
                        val errorBody = response?.errorBody()?.string()
                        errorBody?.let { errorJson ->
                            val error = Gson().fromJson(errorJson, ErrorResponse::class.java)
                            CustomToast(mContext, error?.message ?: "Unknown error")
                        } ?: run {
                            CustomToast(
                                mContext,
                                mContext.getString(R.string.error_something_went_wrong)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("EXCEPTION", "Error: ${e.localizedMessage}")
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }
}