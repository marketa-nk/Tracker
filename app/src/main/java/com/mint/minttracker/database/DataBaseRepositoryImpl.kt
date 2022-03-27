package com.mint.minttracker.database

import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Status
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DataBaseRepositoryImpl @Inject constructor(
    private val tracksDao: TracksDao,
    private val locationDao: MintLocationDao,
) : DataBaseRepository {

    override fun saveLocation(mintLocation: MintLocation, trackId: Long, segment: Int): Single<MintLocation> {
        return Single.fromCallable { mintLocation.copy(idTrack = trackId, segment = segment) }
            .flatMap { locationForInsert ->
                locationDao.insertMintLocation(locationForInsert)
                    .map { locationId -> locationForInsert.copy(id = locationId) }
            }
            .doOnSuccess { println("nata - saved $it, segment - ${it.segment}") }
    }

    override fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return locationDao.getAllRecordsByID(id)
    }

    override fun getLastLocationByTrackId(trackId: Long): Single<MintLocation> {
        return locationDao.getLastLocationByTrackId(trackId)
    }

    override fun createTrack(): Single<Long> {
        return tracksDao.insertTrack(Track(0, System.currentTimeMillis(), Status.STATUS_STARTED))
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
            .flatMap {
                locationDao.deleteMintLocations(track.id)
            }
    }

    override fun getTrackById(id: Long): Single<Track> {
        return tracksDao.getTrackByID(id)
    }

    override fun getTrackAndLocations(): Observable<Map<Track, List<MintLocation>>> {
        return tracksDao.getTrackAndLocations()
    }
}