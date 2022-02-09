package com.mint.minttracker.database

import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_STARTED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DataBaseRepository @Inject constructor(
    private val tracksDao: TracksDao,
    private val locationDao: MintLocationDao
) : IDataBaseRepository{

    override fun saveLocation(mintLocation: MintLocation): Single<MintLocation> {
        return locationDao.insertMintLocation(mintLocation)
            .map { mintLocation.copy(id = it) }
    }

    override fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return locationDao.getAllRecordsByID(id)
    }

    override fun createTrack(): Single<Long> {
        return tracksDao.insertTrack(Track(0, System.currentTimeMillis(), STATUS_STARTED))
            .doOnSuccess { println("Track created - Nata") }
    }

    override fun updateTrack(track: Track) {
        tracksDao.updateTrack(track).also { println("${track.status}") }
    }

    override fun getLastTrack(): Single<Track> {
        return tracksDao.getLastTrack()
            .doOnSuccess { println("Track got last one track - Nata") }
    }

    override fun getAllTracks(): Observable<List<Track>> {
        return tracksDao.getAllTracks()
            .doOnNext { println("You've got all tracks - Nata") }
    }
    override fun deleteTrack(track: Track): Single<Int> {
        return tracksDao.deleteTrack(track).also { println("$track is deleted - Nata") }
    }
    override fun getTrackById(id: Long): Single<Track>{
       return tracksDao.getTrackByID(id)
    }
}