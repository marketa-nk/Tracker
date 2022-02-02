package com.mint.minttracker.historyFragment

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_FINISHED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Record
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal.ROUND_HALF_UP

@InjectViewState
class HistoryPresenter : BasePresenter<HistoryView>() {

    private val dataBaseRepository: DataBaseRepository = DataBaseRepository()

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

    //todo название метода не соотвествует коду - done
    private fun getRecord(id: Long): Single<Record> {
        return dataBaseRepository.getAllLocationsById(id)
            .map {
                if (it.isEmpty()) {
                    Record(
                        idTrack = id,
                        date = 0,
                        distance = 0.0,
                        totalTime = 0,
                        aveSpeed = 0.0,
                        maxSpeed = 0f
                    )
                } else {
                    Record(
                        idTrack = id,
                        date = it.first().time,
                        distance = getDistance(it),
                        totalTime = it.last().time - it.first().time,
                        aveSpeed = it.map { it.speed }.average(),
                        maxSpeed = it.map { it.speed }.maxOrNull() ?: 0.0f
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
            distance += getDistanceBetween(locationList[i], locationList[i + 1])
        }
        return distance
    }

    private fun getDistanceBetween(first: Location, next: Location): Double {
        return (first.distanceTo(next)).toBigDecimal().setScale(2, ROUND_HALF_UP).toDouble() //поделить на 1000 для получения км
    }
}


