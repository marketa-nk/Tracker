package com.mint.minttracker.database

import com.mint.minttracker.App
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_STARTED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single

class DataBaseRepository(
    private val tracksDao: TracksDao = App.instance.database.tracksDao(),
    private val locationDao: MintLocationDao = App.instance.database.mintLocationDao()
) {

    fun saveLocation(mintLocation: MintLocation): Single<MintLocation> {
        return locationDao.insertMintLocation(mintLocation)
            .map { mintLocation.copy(id = it) }
    }

    fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return locationDao.getAllRecordsByID(id)
    }

    fun createTrack(): Single<Long> {
        return tracksDao.insertTrack(Track(0, System.currentTimeMillis(), STATUS_STARTED))
            .doOnSuccess { println("Track created - Nata") }
    }

    fun updateTrack(track: Track) {
        tracksDao.updateTrack(track).also { println("${track.status}") }
    }

    fun getLastTrackId(): Single<Long> {
        return tracksDao.getLastTrackId()
            .doOnSuccess { println("Track got last one idTrack - Nata") }
    }

    fun getLastTrack(): Single<Track> {
        return tracksDao.getLastTrack()
            .doOnSuccess { println("Track got last one track - Nata") }
    }

    fun getAllTracks(): Observable<List<Track>> {
        return tracksDao.getAllTracks()
            .doOnNext { println("You've got all tracks - Nata") }
    }
    fun deleteTrack(track: Track): Single<Int> {
        return tracksDao.deleteTrack(track).also { println("$track is deleted - Nata") }
    }
    fun getTrackById(id: Long): Single<Track>{
       return tracksDao.getTrackByID(id)
    }
}