package com.yggdrasil.apitrackerlite

import android.app.Application
import com.yggdrasil.apitrackerlite.inject.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class ApiTrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApiTrackerApplication)
            loadKoinModules(listOf(uiModule))
        }
    }
}