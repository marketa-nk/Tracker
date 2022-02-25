package com.mint.minttracker.recordFragment

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecordViewModel(
    private val record: Record,
    private val recordInteractor: RecordInteractor,
) : BaseViewModel() {

    val data: MutableLiveData<Record> by lazy { MutableLiveData<Record>().apply { value = record } }
    val points: MutableLiveData<List<LatLng>> by lazy { MutableLiveData<List<LatLng>>() }

    init {
        readyToShowRecord(record.idTrack)
    }

    private fun readyToShowRecord(idTrack: Long) {
        recordInteractor.getListLocationByTrackId(idTrack)
            .map {
                it.map { mintLocation -> mintLocation.latLng }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                points.value = it
            }, {
                points.value = emptyList()
                it.printStackTrace()
            })
            .addDisposable()
    }
}

