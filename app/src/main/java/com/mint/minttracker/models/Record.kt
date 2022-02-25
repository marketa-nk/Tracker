package com.mint.minttracker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Record(
    val idTrack: Long,
    val date: Long,
    val distance: Double,
    val totalTimeMs: Long,
    val aveSpeed: Double,
    val maxSpeed: Float
) : Parcelable
