package com.mint.minttracker.domain.map

import android.location.Location
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single

interface IMapInteractor {
    fun getLastTrack(): Single<Track>
    fun updateTrack(track:Track)
    fun getAllLocationsById(id: Long): Single<List<MintLocation>>
    fun getLocation(): Observable<MintLocation>
}
