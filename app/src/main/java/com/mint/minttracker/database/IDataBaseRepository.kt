package com.mint.minttracker.database

import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single

interface IDataBaseRepository {

    fun saveLocation(mintLocation: MintLocation, trackId: Long): Single<MintLocation>

    fun getAllLocationsById(id: Long): Single<List<MintLocation>>

    fun createTrack(): Single<Long>

    fun updateTrack(track: Track): Single<Track>

    fun getLastTrack(): Single<Track>

    fun getAllTracks(): Observable<List<Track>>

    fun deleteTrack(track: Track): Single<Int>

    fun getTrackById(id: Long): Single<Track>
}