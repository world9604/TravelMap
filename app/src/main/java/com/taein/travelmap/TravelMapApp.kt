package com.taein.travelmap

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TravelMapApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}