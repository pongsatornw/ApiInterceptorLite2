package com.yggdrasil.apitrackerlite.inject

import com.yggdrasil.apitrackerlite.ui.interceptor.detail.InterceptDetailsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object UiModule : BaseModule {

    private val uiModule = module {
        single { InterceptDetailsViewModel() }
    }

    override val module: Module
        get() = uiModule


}