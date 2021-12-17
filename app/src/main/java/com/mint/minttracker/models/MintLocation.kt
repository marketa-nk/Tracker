package com.mint.minttracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class MintLocation(
    @PrimaryKey(autoGenerate = true)
    val idMint: Long,
    val idTrack: Long,
    val time: Long,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float,
    val accuracy: Float,
    ) {

}