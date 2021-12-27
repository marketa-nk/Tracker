package com.mint.minttracker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

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
    val speed: Float,
    val bearing: Float,
    val accuracy: Float,
) : Parcelable {
    @Ignore
    val latLng = LatLng(lat, lon)
}