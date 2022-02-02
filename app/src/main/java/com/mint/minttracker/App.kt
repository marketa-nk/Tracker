package com.mint.minttracker

import android.app.Application
import androidx.room.Room
import com.mint.minttracker.database.AppDatabase
import com.mint.minttracker.services.LocationService

class App : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        LocationService.instance = LocationService(applicationContext)
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration() //todo
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}