package com.stafo.app.screens.expense

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
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.screens.crm.dataClass.LeadCreateResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseViewModel : BaseViewModel() {

    private var mLeadCreate: MutableLiveData<LeadCreateResponse> = MutableLiveData()

    val mLeadCreateResponse: LiveData<LeadCreateResponse> get() = mLeadCreate

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



}