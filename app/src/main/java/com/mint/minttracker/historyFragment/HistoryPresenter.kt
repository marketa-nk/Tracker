package com.mint.minttracker.historyFragment

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_FINISHED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal.ROUND_HALF_UP
import javax.inject.Inject

@AppScope
@InjectViewState
class HistoryPresenter : BasePresenter<HistoryView>() {

    @Inject
    lateinit var dataBaseRepository: DataBaseRepository

    init {
        App.instance.appComponent.injectHistoryPresenter(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getRecordList()
    }

    private fun getRecordList() {
        dataBaseRepository.getAllTracks()
            .switchMap {
                println("trackList ${it.size} - nata")
                Observable.fromIterable(it.filter { track -> track.status == STATUS_FINISHED })
                    .flatMapSingle { track ->
                        println("Track ${track.id} $track - nata")
                        getRecord(track.id)
                    }
                    .toList()
                    .toObservable()
                    .subscribeOn(Schedulers.io())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("RecordList ${it.size} - nata")
                viewState?.showHistory(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getRecord(id: Long): Single<Record> {
        return dataBaseRepository.getAllLocationsById(id)
            .map {
                if (it.isEmpty()) {
                    Record(
                        idTrack = id,
                        date = 0,
                        distance = 0.0,
                        totalTimeMs = 0,
                        aveSpeed = 0.0,
                        maxSpeed = 0f
                    )
                } else {
                    Record(
                        idTrack = id,
                        date = it.first().time,
                        distance = getDistance(it),
                        totalTimeMs = it.last().time - it.first().time,
                        aveSpeed = it.map { it.speedInKm }.average(),
                        maxSpeed = it.maxOf { it.speedInKm }

                    )
                }
            }
    }

    fun recordClicked(record: Record) {
        viewState?.showRecordFragment(record)
    }

    fun deleteRecordButtonClicked(record: Record) {
        dataBaseRepository.getTrackById(record.idTrack)
            .flatMap {
                dataBaseRepository.deleteTrack(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getDistance(list: List<MintLocation>): Double {
        val locationList = list.map { mintLocation ->
            Location("loc").also { location ->
                location.latitude = mintLocation.lat
                location.longitude = mintLocation.lon
            }
        }
        var distance = 0.0
        for (i in 0 until locationList.size - 1) {
            distance += getDistanceBetweenMeters(locationList[i], locationList[i + 1])
        }
        return distance
    }

    private fun getDistanceBetweenMeters(first: Location, next: Location): Double {
        return (first.distanceTo(next)).toBigDecimal().setScale(2, ROUND_HALF_UP).toDouble()
    }
}


