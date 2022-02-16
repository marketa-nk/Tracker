package com.mint.minttracker.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import androidx.room.Room
import com.mint.minttracker.database.*
import com.mint.minttracker.di.components.AppScope

@Module
class DatabaseModule (private val context: Context) {

    @AppScope
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java,"database")
            .build()
    }

    @Provides
    fun provideTracksDao(database: AppDatabase): TracksDao {
        return database.tracksDao()
    }

    @Provides
    fun provideMintLocationDao(database: AppDatabase): MintLocationDao {
        return database.mintLocationDao()
    }

    @AppScope
    @Provides
    fun provideDatabaseRepository(tracksDao: TracksDao, locationDao: MintLocationDao): IDataBaseRepository {
        return DataBaseRepository(tracksDao, locationDao)
    }
}