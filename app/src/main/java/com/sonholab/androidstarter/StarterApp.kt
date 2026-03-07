package com.sonholab.androidstarter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StarterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Global app initialization (analytics, crash reporting, etc.)
    }
}
