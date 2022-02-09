package com.mint.minttracker.domain.map

import com.mint.minttracker.database.IDataBaseRepository
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import com.mint.minttracker.services.LocationService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapInteractor @Inject constructor(private val dataBaseRepository: IDataBaseRepository, private val locationService: LocationService) : IMapInteractor {

    override fun getLastTrack(): Single<Track> {
        return dataBaseRepository.getLastTrack()
    }

    override fun updateTrack(track: Track) {
        return dataBaseRepository.updateTrack(track)//todo переделать на
    }

    override fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return dataBaseRepository.getAllLocationsById(id)
    }

    override fun getLocation(): Observable<MintLocation> {
        return locationService.getLocation()
            .observeOn(Schedulers.io())
            .map { location ->
                MintLocation(0, 0, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
            }
    }


}



