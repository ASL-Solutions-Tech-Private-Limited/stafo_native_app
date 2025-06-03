package com.stafo.app.screens.tripPlan

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.stafo.app.ASLEmpMng
import com.stafo.app.R
import com.stafo.app.base.BaseViewModel
import com.stafo.app.base.model.ErrorResponse
import com.stafo.app.screens.auth.LoginActivity
import com.stafo.app.screens.tripPlan.dataClass.AddVehicleResponse
import com.stafo.app.screens.tripPlan.dataClass.CreateTripResponse
import com.stafo.app.screens.tripPlan.dataClass.DriverListResponse
import com.stafo.app.screens.tripPlan.dataClass.TripDashboardResponse
import com.stafo.app.screens.tripPlan.dataClass.VehicleListResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TripViewModel : BaseViewModel() {


    private var mTripDashboard: MutableLiveData<TripDashboardResponse> = MutableLiveData()
    val mTripDashboardResponse: LiveData<TripDashboardResponse> get() = mTripDashboard

    private var mVehicleList: MutableLiveData<VehicleListResponse> = MutableLiveData()
    val mVehicleListResponse: LiveData<VehicleListResponse> get() = mVehicleList

    private var mDriverList: MutableLiveData<DriverListResponse> = MutableLiveData()
    val mDriverListResponse: LiveData<DriverListResponse> get() = mDriverList

    private var mCreateTrip: MutableLiveData<CreateTripResponse> = MutableLiveData()
    val mCreateTripResponse: LiveData<CreateTripResponse> get() = mCreateTrip

    private var mAddVehicle: MutableLiveData<AddVehicleResponse> = MutableLiveData()
    val mAddVehicleResponse: LiveData<AddVehicleResponse> get() = mAddVehicle


    fun getVehicleList(mContext: Context) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callVehicleList()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mVehicleList.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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

    fun getDriverList(mContext: Context) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDriverList()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDriverList.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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

    fun createTrip(mContext: Context, request: HashMap<String, Any>) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCreateTrip(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCreateTrip.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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

    fun getTripDashboardData(mContext: Context) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callTripDashboardInfo()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mTripDashboard.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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

    fun createVehicle(
        mContext: Context,
        map: HashMap<String, RequestBody>,
        filePart: MultipartBody.Part?
    ) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAddVehicle(map, filePart)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAddVehicle.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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

    fun checkDriverAvailability(
        mContext: Context,
        driverId: String,
    ) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = HashMap<String, Any>()
                request["driver_id"] = driverId
                val response = ASLEmpMng.instance.apiStores()?.callCheckDriverAvailability(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAddVehicle.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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


    fun checkVehicleAvailability(
        mContext: Context,
        vehicleId: String,
    ) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = HashMap<String, Any>()
                request["vehicle_id"] = vehicleId
                val response = ASLEmpMng.instance.apiStores()?.callCheckVehicleAvailability(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAddVehicle.postValue(it.body())
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
                            mContext, mContext.getString(R.string.error_something_went_wrong)
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