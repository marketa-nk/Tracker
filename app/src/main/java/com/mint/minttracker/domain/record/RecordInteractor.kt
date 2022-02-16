package com.mint.minttracker.domain.record

import com.mint.minttracker.models.MintLocation
import io.reactivex.Single

interface RecordInteractor {
    fun getListLocationByTrackId(trackId: Long): Single<List<MintLocation>>
}
