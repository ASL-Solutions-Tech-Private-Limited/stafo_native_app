package com.stafo.app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private var loaderLiveData: MutableLiveData<String>? = null

    fun getLoaderLiveData(): MutableLiveData<String> {
        if (loaderLiveData == null)
            loaderLiveData = MutableLiveData()

        return loaderLiveData as MutableLiveData<String>
    }




}