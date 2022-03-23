package com.mint.minttracker.domain.history

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.getTotalTimeInMillis
import com.mint.minttracker.models.Record
import com.mint.minttracker.models.Status
import io.reactivex.Observable
import io.reactivex.Single
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
                                durationMs = 0,
                                totalTimeMs = 0,
                                aveSpeedInMeters = 0.0,
                                maxSpeedInMeters = 0f
                            )
                        } else {
                            Record(
                                idTrack = it.key.id,
                                date = it.value.first().time,
                                distance = LocationUtils.calcDistanceMeters(it.value),
                                durationMs = it.value.getTotalTimeInMillis(),
                                totalTimeMs = it.value.last().time - it.value.first().time,
                                aveSpeedInMeters = it.value.map { it.speedInMeters }.average(),
                                maxSpeedInMeters = it.value.maxOf { it.speedInMeters }
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
}