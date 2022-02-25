package com.mint.minttracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity
data class Track(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTrack")
    val id: Long,
    val date: Long,
    val status: Status
)