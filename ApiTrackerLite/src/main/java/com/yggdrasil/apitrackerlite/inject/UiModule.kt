package com.yggdrasil.apitrackerlite.inject

import com.yggdrasil.apitrackerlite.ui.interceptor.detail.InterceptDetailsViewModel
import org.koin.dsl.module

val uiModule = module {

    single { InterceptDetailsViewModel() }

}