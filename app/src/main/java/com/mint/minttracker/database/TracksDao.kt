package com.mint.minttracker.database

import androidx.room.*
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: Track): Single<Long>

    @Update
    fun updateTrack(track: Track)

//    @Query("SELECT * FROM track")
//    fun getAllRecords(): List<Track>
//
    @Query("SELECT COUNT(id) FROM track")
    fun getCount(): Int

    @Query("SELECT id FROM track ORDER BY id DESC LIMIT 1")
    fun getLastTrackId(): Single<Long>

    @Query("SELECT * FROM track ORDER BY id DESC LIMIT 1")
    fun getLastTrack(): Single<Track>
}
