package com.mint.minttracker.domain.location

import com.mint.minttracker.locationRepository.LocationRepository
import com.mint.minttracker.models.MintLocation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface LocationInteractor {

    fun getLocation(): Observable<MintLocation>

    fun getLastLocation(): Single<MintLocation>
}
