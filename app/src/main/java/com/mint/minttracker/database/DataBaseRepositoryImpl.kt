package com.mint.minttracker.database

import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_STARTED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DataBaseRepositoryImpl @Inject constructor(
    private val tracksDao: TracksDao,
    private val locationDao: MintLocationDao,
) : DataBaseRepository {

    override fun saveLocation(mintLocation: MintLocation, trackId: Long): Single<MintLocation> {
        return locationDao.insertMintLocation(mintLocation.copy(idTrack = trackId))
            .map { mintLocation.copy(id = it) }
            .doOnSuccess { println("nata - saved $it") }
    }

    override fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return locationDao.getAllRecordsByID(id)
    }

    override fun createTrack(): Single<Long> {
        return tracksDao.insertTrack(Track(0, System.currentTimeMillis(), STATUS_STARTED))
            .doOnSuccess { println("Track created - Nata") }
    }

    override fun updateTrack(track: Track): Single<Track> {
        return tracksDao.updateTrack(track)
            .andThen(Single.just(track))
            .doOnSuccess { println("Track updated - Nata") }

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

    override fun getTrackById(id: Long): Single<Track> {
        return tracksDao.getTrackByID(id)
    }
    override fun getTrackAndLocations(): Observable<Map<Track, List<MintLocation>>> {
        return tracksDao.getTrackAndLocations()
    }
}