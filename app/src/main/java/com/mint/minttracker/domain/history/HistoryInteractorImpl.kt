package com.mint.minttracker.domain.history

import android.location.Location
import com.mint.minttracker.database.IDataBaseRepository
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_FINISHED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

class HistoryInteractorImpl @Inject constructor(private val dataBaseRepository: IDataBaseRepository): HistoryInteractor {

    override fun loadHistory(): Observable<List<Record>> {
        return dataBaseRepository.getAllTracks()
            .map { it.filter { track -> track.status == STATUS_FINISHED } }
            .switchMap {
                Observable.fromIterable(it)
                    .flatMapSingle { track ->
                        println("Track ${track.id} $track - nata")
                        getRecord(track.id)
                    }
                    .toList()
                    .toObservable()
            }
    }

    override fun deleteRecord(record: Record): Single<Int> {
        return dataBaseRepository.getTrackById(record.idTrack)
            .flatMap {
                dataBaseRepository.deleteTrack(it)
            }
    }

    private fun getRecord(id: Long): Single<Record> {
        return dataBaseRepository.getAllLocationsById(id)
            .map {
                if (it.isEmpty()) {
                    Record(
                        idTrack = id,
                        date = 0,
                        distance = 0.0,
                        totalTimeMs = 0,
                        aveSpeed = 0.0,
                        maxSpeed = 0f
                    )
                } else {
                    Record(
                        idTrack = id,
                        date = it.first().time,
                        distance = getDistance(it),
                        totalTimeMs = it.last().time - it.first().time,
                        aveSpeed = it.map { it.speedInKm }.average(),
                        maxSpeed = it.maxOf { it.speedInKm }

                    )
                }
            }
    }

    private fun getDistance(list: List<MintLocation>): Double {
        val locationList = list.map { mintLocation ->
            Location("loc").also { location ->
                location.latitude = mintLocation.lat
                location.longitude = mintLocation.lon
            }
        }
        var distance = 0.0
        for (i in 0 until locationList.size - 1) {
            distance += getDistanceBetweenMeters(locationList[i], locationList[i + 1])
        }
        return distance
    }

    private fun getDistanceBetweenMeters(first: Location, next: Location): Double {
        return (first.distanceTo(next)).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}