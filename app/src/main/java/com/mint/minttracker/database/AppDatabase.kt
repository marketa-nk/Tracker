package com.mint.minttracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track

@Database(entities = [Track::class, MintLocation::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun mintLocationDao(): MintLocationDao
}
