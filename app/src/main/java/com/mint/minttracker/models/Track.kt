package com.mint.minttracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
class Track (
    @PrimaryKey(autoGenerate = true)
    val idTrack: Long,
    val date: Long
        )