package com.mint.minttracker

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationService.instance = LocationService(applicationContext)
    }

}