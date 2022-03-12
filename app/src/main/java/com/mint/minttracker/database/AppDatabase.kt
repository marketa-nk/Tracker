package com.mint.minttracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track

@Database(entities = [Track::class, MintLocation::class], version = 6)
@TypeConverters(StatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun mintLocationDao(): MintLocationDao
}
