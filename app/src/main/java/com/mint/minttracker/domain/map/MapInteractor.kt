package com.mint.minttracker.domain.map

import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single

interface MapInteractor {
    fun getLastTrack(): Single<Track>
    fun updateTrack(track: Track): Single<Track>
    fun getAllLocationsById(id: Long): Single<List<MintLocation>>
}
