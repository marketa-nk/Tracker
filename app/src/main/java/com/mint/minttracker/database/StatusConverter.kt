package com.mint.minttracker.database

import androidx.room.TypeConverter
import com.mint.minttracker.models.Status

class StatusConverter {

    @TypeConverter
    fun fromStatus(status: Status) = status.name

    @TypeConverter
    fun toStatus(str: String) = enumValueOf<Status>(str)
}
