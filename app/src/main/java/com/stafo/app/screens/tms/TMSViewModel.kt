package com.stafo.app.screens.tms

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
import com.stafo.app.screens.crm.dataClass.TaskListResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TMSViewModel : BaseViewModel() {

    private var mTaskList: MutableLiveData<TaskListResponse> = MutableLiveData()



    val mTaskListResponse: LiveData<TaskListResponse> get() = mTaskList

    fun getTaskList(mContext: Context, comId: Int) {

        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callTaskList(comId)
                Log.d("crm", "lead update: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mTaskList.postValue(it.body())
                        } else {
                            when(it.code()){
                                401->{
                                    doLogout(mContext)
                                }
                            }
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