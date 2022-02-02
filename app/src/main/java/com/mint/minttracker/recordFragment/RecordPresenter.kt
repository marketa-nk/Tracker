package com.mint.minttracker.recordFragment

import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@InjectViewState
class RecordPresenter : BasePresenter<RecordView>() {
    private val dataBaseRepository: DataBaseRepository = DataBaseRepository()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun readyToShowRecord(record: Record) {

        viewState?.showRecordInfo(record)

        dataBaseRepository.getTrackById(record.idTrack)
            .flatMap {
                dataBaseRepository.getAllLocationsById(it.id)
            }
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
