package com.stafo.app.screens.billpayment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stafo.app.ASLEmpMng
import com.stafo.app.R
import com.stafo.app.base.BaseViewModel
import com.stafo.app.base.model.ErrorResponse
import com.stafo.app.screens.auth.LoginActivity
import com.stafo.app.screens.billpayment.dataClass.BbpsOperatorDetailsResponse
import com.stafo.app.screens.billpayment.dataClass.BillerInputParamsRaw
import com.stafo.app.screens.billpayment.dataClass.CategoryMenuResponse
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorRequest
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorResponse
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.chat.dataClass.ChatResponse
import com.stafo.app.screens.chat.dataClass.SendChatRequest
import com.stafo.app.screens.chat.dataClass.SendChatResponse
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
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.utils.CustomToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillPaymentsViewModel : BaseViewModel() {

    private var mElectricityOperator: MutableLiveData<ElectricityOperatorResponse> =
        MutableLiveData()

    val mElectricityOperatorResponse: LiveData<ElectricityOperatorResponse> get() = mElectricityOperator

    private var mCategoryMenu: MutableLiveData<CategoryMenuResponse> = MutableLiveData()

    val mCategoryMenuResponse: LiveData<CategoryMenuResponse> get() = mCategoryMenu


    private var mChat: MutableLiveData<ChatResponse> = MutableLiveData()

    val mChatResponse: LiveData<ChatResponse> get() = mChat


    private var mPerformanceType: MutableLiveData<PerformanceTypeResponse> = MutableLiveData()

    val mPerformanceTypeResponse: LiveData<PerformanceTypeResponse> get() = mPerformanceType

    private var mAddPerformance: MutableLiveData<AddPerformanceResponse> = MutableLiveData()

    val mAddPerformanceResponse: LiveData<AddPerformanceResponse> get() = mAddPerformance

    private var mSendChat: MutableLiveData<SendChatResponse> = MutableLiveData()

    val mSendChatResponse: LiveData<SendChatResponse> get() = mSendChat


    private var mDeletePerformance: MutableLiveData<DeletePerformanceResponse> = MutableLiveData()

    val mDeletePerformanceResponse: LiveData<DeletePerformanceResponse> get() = mDeletePerformance


    private var mUpdatePerformance: MutableLiveData<DeletePerformanceResponse> = MutableLiveData()

    val mUpdatePerformanceResponse: LiveData<DeletePerformanceResponse> get() = mUpdatePerformance


    private var mPerformanceAdd: MutableLiveData<PerformanceAddResponse> = MutableLiveData()

    val mPerformanceAddResponse: LiveData<PerformanceAddResponse> get() = mPerformanceAdd


    private var mRankList: MutableLiveData<RankListResponse> = MutableLiveData()


    val mRankListResponse: LiveData<RankListResponse> get() = mRankList

    private var mPoints: MutableLiveData<PointsResponse> = MutableLiveData()


    val mPointsResponse: LiveData<PointsResponse> get() = mPoints


    private var mOperatorDetails: MutableLiveData<BbpsOperatorDetailsResponse> = MutableLiveData()


    val mOperatorDetailsResponse: LiveData<BbpsOperatorDetailsResponse> get() = mOperatorDetails



    fun getOperatorDetails(mContext: Context, operatorCode: String) {
        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callOperatorDetails(operatorCode)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response?.isSuccessful == true) {
                        val jsonBody = response.body()?.string()
                        val gson = Gson()
                        val operatorDetails = gson.fromJson(jsonBody, BbpsOperatorDetailsResponse::class.java)

                        try {
                            val rawParams = operatorDetails.mdmRequestNew.biller.billerInputParams
                            val inputFields = rawParams.toNormalized()

                            if (inputFields.isNotEmpty()) {
                                Log.d("paramInfo", inputFields.toString())
                                mOperatorDetails.postValue(operatorDetails)
                            } else {
                                CustomToast(mContext, "No input fields found")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            CustomToast(mContext, "Error parsing input fields: ${e.localizedMessage}")
                        }

                    } else {
                        CustomToast(mContext, "Error: ${response?.code()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, "Exception: ${e.localizedMessage}")
                }
            }
        }
    }











    fun viewPointsDetails(mContext: Context,request: PointsRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callPointsDetail(request)
                Log.d("res", "point details list : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPoints.postValue(it.body())
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



    fun viewRankList(mContext: Context,request: RankListRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callRankListEmp(request)
                Log.d("res", "rank list : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mRankList.postValue(it.body())
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


    fun savePerformance(mContext: Context,request: PerformanceAddRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callSaveEmpPerformance(request)
                Log.d("res", "performance  save : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPerformanceAdd.postValue(it.body())
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


    fun updatePerformanceType(mContext: Context, id:Int,request:AddPerformanceRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callUpdatePerformance(id,request)
                Log.d("res", "performance type update : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mUpdatePerformance.postValue(it.body())
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


    fun deletePerformanceType(mContext: Context, id:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callDeletePerformance(id)
                Log.d("res", "performance type delete : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDeletePerformance.postValue(it.body())
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


    fun sendChatRequest(mContext: Context, request: SendChatRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callSendChat(request)
                Log.d("res", "send chat data : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mSendChat.postValue(it.body())
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


    fun createPerformance(mContext: Context, request: AddPerformanceRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callAddPerformance(request)
                Log.d("res", "performance type create : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAddPerformance.postValue(it.body())
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

    fun getPerformanceTypeList(mContext: Context, request: ChatRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callPerformanceType(request)
                Log.d("res", "performance type list : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mPerformanceType.postValue(it.body())
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

    fun getChatList(mContext: Context, request: ChatRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callChatAdmin(request)
                Log.d("res", "chatAdmin : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mChat.postValue(it.body())
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


    fun getMenuList(mContext: Context) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callCategoryMenu()
                Log.d("res", "menu list : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mCategoryMenu.postValue(it.body())
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

    fun getElectricityOperator(mContext: Context, request: ElectricityOperatorRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = ASLEmpMng.instance.apiStores()?.callElectricityOperator(request)
                Log.d("res", "callElectricityOperator : ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mElectricityOperator.postValue(it.body())
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