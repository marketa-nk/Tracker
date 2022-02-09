package com.mint.minttracker.domain.record

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.database.IDataBaseRepository
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import io.reactivex.Single
import javax.inject.Inject

class RecordInteractor @Inject constructor(private val dataBaseRepository: IDataBaseRepository) : IRecordInteractor {

    override fun showTrackInfo(record: Record): Single<List<MintLocation>> {
        return dataBaseRepository.getTrackById(record.idTrack)
            .flatMap {
                dataBaseRepository.getAllLocationsById(it.id)
            }
    }
}
