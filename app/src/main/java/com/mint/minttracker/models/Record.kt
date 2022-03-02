package com.mint.minttracker.models

import android.os.Parcelable
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Record(
    val idTrack: Long,
    val date: Long,
    val distance: Double,
    val totalTimeMs: Long,
    val aveSpeedInMeters: Double,
    val maxSpeedInMeters: Float
) : Parcelable{
    @IgnoredOnParcel
    @Ignore
    val aveSpeedInKm: Double = (aveSpeedInMeters * 3.6)
    @IgnoredOnParcel
    @Ignore
    val maxSpeedInKm: Double = (maxSpeedInMeters * 3.6)
}
