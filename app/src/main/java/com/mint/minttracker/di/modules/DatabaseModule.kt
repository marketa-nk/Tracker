package com.mint.minttracker.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mint.minttracker.database.*
import com.mint.minttracker.di.components.AppScope
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(private val context: Context) {

    @AppScope
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
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
    fun provideDatabaseRepository(tracksDao: TracksDao, locationDao: MintLocationDao): DataBaseRepository {
        return DataBaseRepositoryImpl(tracksDao, locationDao)
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `track_new` (`idTrack` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `status` TEXT NOT NULL)")
            database.execSQL("INSERT INTO track_new (idTrack, date, status) SELECT id, date, status FROM track")
            database.execSQL("DROP TABLE track")
            database.execSQL("ALTER TABLE track_new RENAME TO track")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE mintLocation ADD COLUMN segment INTEGER DEFAULT 0 NOT NULL")
        }
    }
}