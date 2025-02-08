package com.asl_emp_mng.app.screens.auth

import android.content.Context
import com.asl_emp_mng.app.base.BaseViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.asl_emp_mng.app.ASLEmpMng
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.model.ErrorResponse
import com.asl_emp_mng.app.screens.auth.dataClass.CompanyTypeResponse
import com.asl_emp_mng.app.utils.CustomToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel() : BaseViewModel() {


    private var mCompanyType: MutableLiveData<CompanyTypeResponse> = MutableLiveData()
    val mCompanyTypeResponse: LiveData<CompanyTypeResponse> get() = mCompanyType


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
}