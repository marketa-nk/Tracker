package com.mint.minttracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Long,
    val status: String
)