package com.mint.minttracker.recordFragment.mapper

import com.mint.minttracker.models.Record
import com.mint.minttracker.msToRecordUiString
import com.mint.minttracker.recordFragment.model.RecordUi
import com.mint.minttracker.toUiString
import java.text.DateFormat

class RecordUiMapper {
    operator fun invoke(record: Record) = RecordUi(
        idTrack = record.idTrack,
        date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(record.date),
        distance = (record.distanceKm).toUiString() + " km",
        duration = (record.distanceKm).toUiString() + " km",
        totalTime = record.totalTimeMs.msToRecordUiString(),
        stopTime = record.stopTimeMs.msToRecordUiString(),
        speedMax = (record.maxSpeedInKm).toUiString() + " km/h",
        speedAve = (record.aveSpeedInKm).toUiString() + " km/h",
    )
}
