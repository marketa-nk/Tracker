package com.mint.minttracker.domain.history

import com.mint.minttracker.models.Record
import io.reactivex.Observable
import io.reactivex.Single

interface IHistoryInteractor {
    fun loadHistory(): Observable<List<Record>>
    fun deleteRecord(record: Record): Single<Int>
}
