package com.mint.minttracker.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class MintLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val idTrack: Long,
    val time: Long,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
    @ColumnInfo(name = "speed")
    val speedInMeters: Float,
    val bearing: Float,
    val accuracy: Float,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    val latLng = LatLng(lat, lon)
    @IgnoredOnParcel
    @Ignore
    val speedInKm: Float = speedInMeters * 3.6f
}