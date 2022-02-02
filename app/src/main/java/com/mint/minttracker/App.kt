package com.mint.minttracker

import android.app.Application
import androidx.room.Room
import com.mint.minttracker.database.AppDatabase
import com.mint.minttracker.services.LocationService
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.migration.Migration




class App : Application() {

    lateinit var database: AppDatabase

    val MIGRATION_1_2: Migration = object : Migration(1, 2) { //todo когда потребуется миграция
        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE Employee ADD COLUMN birthday INTEGER DEFAULT 0 NOT NULL")
        }
    }
    val MIGRATION_2_3: Migration = object : Migration(2, 3) { //todo когда потребуется миграция
        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE Employee ADD COLUMN birthday INTEGER DEFAULT 0 NOT NULL")
        }
    }

    override fun onCreate() {
        super.onCreate()
        LocationService.instance = LocationService(applicationContext)
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration() //todo
//            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) //todo когда потребуется миграция
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}