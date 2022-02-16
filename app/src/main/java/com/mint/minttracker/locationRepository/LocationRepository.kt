package com.mint.minttracker.locationRepository

import android.location.Location
import com.mint.minttracker.models.MintLocation
import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {

    fun getLocation(): Observable<Location>

    fun getLastLocation(): Single<MintLocation>

}

