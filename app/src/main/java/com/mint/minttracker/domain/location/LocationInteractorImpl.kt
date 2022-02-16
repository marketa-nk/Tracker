package com.mint.minttracker.domain.location

import com.mint.minttracker.locationRepository.LocationRepository
import com.mint.minttracker.models.MintLocation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LocationInteractorImpl @Inject constructor(private val locationService: LocationRepository) : LocationInteractor {

    override fun getLocation(): Observable<MintLocation> {
        return locationService.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { location ->
                MintLocation(0, 0, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
            }
    }

    override fun getLastLocation(): Single<MintLocation> {
        return locationService.getLastLocation()
    }
}
