package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yggdrasil.apitrackerlite.interceptor.ApiInterceptor


// Http Catcher, Get It, Get_To, Watcher, Http Inspector, Catch It, Plog, Log_P, LogP, Got It,
// Just Log It, Doge It, Just Log It, Logary, HTTPetch, loggaroo
class InterceptDetailsViewModel: ViewModel() {

    private val _interceptorObject = MutableLiveData<ApiInterceptor.Params>()
    val interceptorObject: LiveData<ApiInterceptor.Params> get() = _interceptorObject

    fun setInterceptorData(params: ApiInterceptor.Params) {
        _interceptorObject.value = params
    }
}