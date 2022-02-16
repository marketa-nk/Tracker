package com.mint.minttracker.recordFragment

import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class RecordPresenter : BasePresenter<RecordView>() {
    @Inject
    lateinit var recordInteractor: RecordInteractor

    init {
        App.instance.appComponent.injectRecordPresenter(this)
    }

    fun readyToShowRecord(record: Record) {

        viewState?.showRecordInfo(record)

        recordInteractor.getListLocationByTrackId(record.idTrack)
            .map {
                it.map { mintLocation -> mintLocation.latLng }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.showPolyline(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }
}
