package com.yggdrasil.apitrackerlite.inject

import org.koin.core.module.Module

internal interface BaseModule {

    abstract val module: Module
}