package com.mint.minttracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: Track): Single<Long>

    @Query("SELECT * FROM track")
    fun getAllRecords(): List<Track>

    @Query("SELECT COUNT(idTrack) FROM track")
    fun getCount(): Int
}
