package com.mint.minttracker.domain.record

import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import io.reactivex.Observable
import io.reactivex.Single

interface IRecordInteractor {
    fun showTrackInfo(record: Record):  Single<List<MintLocation>>
}
