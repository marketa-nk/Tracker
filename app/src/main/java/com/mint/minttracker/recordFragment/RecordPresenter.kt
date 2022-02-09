package com.mint.minttracker.recordFragment

import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.domain.record.IRecordInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AppScope
@InjectViewState
class RecordPresenter : BasePresenter<RecordView>() {
    @Inject
    lateinit var iRecordInteractor: IRecordInteractor

    init {
        App.instance.appComponent.injectRecordPresenter(this)
    }

    fun readyToShowRecord(record: Record) {

        viewState?.showRecordInfo(record)

        iRecordInteractor.showTrackInfo(record)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.showPolyline(
                    it.map { mintLocation ->
                        mintLocation.latLng
                    })
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }
}
