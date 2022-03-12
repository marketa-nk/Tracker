package com.mint.minttracker.domain.history

import android.location.Location
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.getTotalTimeInMillis
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import com.mint.minttracker.models.Status
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

class HistoryInteractorImpl @Inject constructor(private val dataBaseRepository: DataBaseRepository) : HistoryInteractor {

    override fun loadHistory(): Observable<List<Record>> {
        return dataBaseRepository.getTrackAndLocations()
            .map { values ->
                values
                    .filter {
                        it.key.status == Status.STATUS_FINISHED
                    }
                    .map {
                        if (it.value.isEmpty()) {
                            Record(
                                idTrack = it.key.id,
                                date = 0,
                                distance = 0.0,
                                totalTimeMs = 0,
                                aveSpeedInMeters = 0.0,
                                maxSpeedInMeters = 0f
                            )
                        } else {
                            Record(
                                idTrack = it.key.id,
                                date = it.value.first().time,
                                distance = getDistance(it.value),
                                totalTimeMs = it.value.getTotalTimeInMillis(),
                                aveSpeedInMeters = it.value.map { it.speedInKm }.average(),
                                maxSpeedInMeters = it.value.maxOf { it.speedInKm }
                            )
                        }
                    }
            }
    }

    override fun deleteRecord(record: Record): Single<Int> {
        return dataBaseRepository.getTrackById(record.idTrack)
            .flatMap {
                dataBaseRepository.deleteTrack(it)
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