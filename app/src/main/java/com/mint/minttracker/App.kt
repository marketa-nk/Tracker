package com.mint.minttracker

import android.app.Application
import com.mint.minttracker.di.components.AppComponent
import com.mint.minttracker.di.components.DaggerAppComponent
import com.mint.minttracker.di.modules.ContextModule
import com.mint.minttracker.di.modules.DatabaseModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .databaseModule(DatabaseModule(this))
            .build()

        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}