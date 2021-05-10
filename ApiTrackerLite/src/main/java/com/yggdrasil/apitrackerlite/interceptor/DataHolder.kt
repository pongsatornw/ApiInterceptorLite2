package com.yggdrasil.apitrackerlite.interceptor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

internal object DataHolder {

    private val data = mutableListOf<ApiInterceptor.Params>()

    private val _onDataChange: MutableLiveData<Unit> = MutableLiveData<Unit>()
    val onDataChange: LiveData<Unit> get() = _onDataChange

    fun addData(obj: ApiInterceptor.Params) {
        data.add(obj.apply {
            onTimeout = {
                data.remove(this)
                _onDataChange.postValue(Unit)
            }
        })
    }

    fun getData() = data

    fun getLatestData(): ApiInterceptor.Params? = data.lastOrNull()

    fun getDataByTimeStamp(timeStamp: Long?): ApiInterceptor.Params? =
        data.firstOrNull { it.timeStamp == (timeStamp ?: -1L)}

    fun clearData() = data.clear()
}