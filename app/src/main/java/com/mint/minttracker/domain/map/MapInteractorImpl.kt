package com.mint.minttracker.domain.map

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(private val dataBaseRepository: DataBaseRepository) : MapInteractor {

    override fun getLastTrack(): Single<Track> {
        return dataBaseRepository.getLastTrack()
    }

    override fun updateTrack(track: Track): Single<Track> {
        return dataBaseRepository.updateTrack(track)
    }

    override fun getAllLocationsById(id: Long): Single<List<MintLocation>> {
        return dataBaseRepository.getAllLocationsById(id)
    }

    override fun deleteCurrentTrack(): Single<Int> {
        return dataBaseRepository.getLastTrack()
            .flatMap {
                dataBaseRepository.deleteTrack(it)
            }
    }
}



