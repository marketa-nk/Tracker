package com.mint.minttracker.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import com.mint.minttracker.database.AppDatabase
import androidx.room.Room
import com.mint.minttracker.database.MintLocationDao
import com.mint.minttracker.database.TracksDao
import com.mint.minttracker.di.components.AppScope

@Module
class DatabaseModule (private val context: Context) {
    @AppScope
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java,"database")
            .build()
    }

    @AppScope
    @Provides
    fun provideTracksDao(database: AppDatabase): TracksDao {
        return database.tracksDao()
    }

    @AppScope
    @Provides
    fun provideMintLocationDao(database: AppDatabase): MintLocationDao {
        return database.mintLocationDao()
    }
}