package com.mint.minttracker.database

import androidx.room.*
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: Track): Single<Long>

    @Update
    fun updateTrack(track: Track): Completable

    @Delete
    fun deleteTrack(track: Track): Single<Int>

    @Query("SELECT idTrack FROM track ORDER BY idTrack DESC LIMIT 1")
    fun getLastTrackId(): Single<Long>

    @Query("SELECT * FROM track ORDER BY idTrack DESC LIMIT 1")
    fun getLastTrack(): Single<Track>

    @Query("SELECT * FROM track")
    fun getAllTracks(): Observable<List<Track>>

    @Query("SELECT * FROM track WHERE idTrack = :id")
    fun getTrackByID(id: Long): Single<Track>

    @Query("SELECT * FROM Track JOIN MintLocation ON Track.idTrack = MintLocation.idTrack ")
    fun getTrackAndLocations(): Observable<Map<Track, List<MintLocation>>>
}
