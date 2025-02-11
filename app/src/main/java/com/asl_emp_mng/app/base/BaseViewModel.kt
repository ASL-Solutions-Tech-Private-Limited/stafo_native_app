package com.asl_emp_mng.app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.OwnerInfo

open class BaseViewModel : ViewModel() {

    private var loaderLiveData: MutableLiveData<String>? = null

    fun getLoaderLiveData(): MutableLiveData<String> {
        if (loaderLiveData == null)
            loaderLiveData = MutableLiveData()

        return loaderLiveData as MutableLiveData<String>
    }




}