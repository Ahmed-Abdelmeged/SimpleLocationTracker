package com.ahmedabdelmeged.simplelocationtracker

import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //Using timber for logging so we have a center place to log every thing to the logging services
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}