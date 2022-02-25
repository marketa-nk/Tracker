package com.mint.minttracker.recordFragment

import java.util.*

data class RecordModel(
    val idTrack: Long,
    val date: Date,
    val distance: String,
    val totalTimeMs: String,
    val aveSpeed: String,
    val maxSpeed: String,
)