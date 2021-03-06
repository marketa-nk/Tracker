package com.mint.minttracker.domain.record

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.MintLocation
import io.reactivex.Single
import javax.inject.Inject

class RecordInteractorImpl @Inject constructor(private val dataBaseRepository: DataBaseRepository) : RecordInteractor {

    override fun getListLocationByTrackId(trackId: Long): Single<List<MintLocation>> {
        return dataBaseRepository.getAllLocationsById(trackId)
    }
}
