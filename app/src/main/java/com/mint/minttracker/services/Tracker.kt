package com.mint.minttracker.services

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.domain.location.LocationInteractor
import com.mint.minttracker.getTotalTimeInMillis
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Status
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AppScope
class Tracker @Inject constructor(
    private val dataBaseRepository: DataBaseRepository,
    private val locationInteractor: LocationInteractor,
    private val stopWatch: StopWatch,
) {
    private val disposableLocationUpdates = SerialDisposable()

    private val compositeDisposable = CompositeDisposable()

    private var locationPublishSubject: Subject<MintLocation> = PublishSubject.create()
    var location: Observable<MintLocation> = locationPublishSubject.hide()

    val timeInSec: Observable<Long> = Observable.interval(200, TimeUnit.MILLISECONDS)
        .map { stopWatch.getTimeSeconds() }

    private var firstStart = true


    fun start() {
        stopWatch.start(firstStart, 0)
        firstStart = false
        dataBaseRepository.createTrack()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                saveLocationUpdates(it, 0)
                println("nata - segment 0")
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun resume(status: Status) {
        resumeStopWatcher(firstStart)
        dataBaseRepository.getLastTrack()
            .flatMap { track ->
                dataBaseRepository.updateTrack(track.copy(status = status))
            }
            .flatMap { track ->
                dataBaseRepository.getLastLocationByTrackId(track.id)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                saveLocationUpdates(it.idTrack, it.segment + 1)
                println("nata - segment ${it.segment} + 1")
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun stop(status: Status) {
        if (status == Status.STATUS_FINISHED) {
            firstStart = true
        }
        stopWatch.pause()
        disposableLocationUpdates.set(null)
        dataBaseRepository.getLastTrack()
            .flatMap { track ->
                dataBaseRepository.getAllLocationsById(track.id)
                    .map { track to it }
            }
            .flatMap { (track, locations) ->
                if (locations.isEmpty() && status == Status.STATUS_FINISHED) {
                    dataBaseRepository.deleteTrack(track)
                } else {
                    dataBaseRepository.updateTrack(Track(track.id, track.date, status))
                        .doOnSuccess {
                            println("$status onStopTracker статус $status Nata")
                        }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                it.printStackTrace()
            })
            .addDisposable()

    }

    private fun saveLocationUpdates(trackId: Long, segment: Int) {
        locationInteractor.getLocation()
            .concatMapSingle { location ->
                dataBaseRepository.saveLocation(location, trackId, segment)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                locationPublishSubject.onNext(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable(disposableLocationUpdates)
    }


    private fun resumeStopWatcher(firstStart: Boolean) {
        dataBaseRepository.getLastTrack()
            .flatMap {
                if (it.status != Status.STATUS_FINISHED) {
                    dataBaseRepository.getAllLocationsById(it.id)
                } else {
                    Single.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                stopWatch.start(firstStart, it.getTotalTimeInMillis())
                if (firstStart) {
                    this.firstStart = false
                }
            }) {
                it.printStackTrace()
            }
            .addDisposable()
    }


    private fun Disposable.addDisposable(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    private fun Disposable.addDisposable(serialDisposable: SerialDisposable): Disposable {
        serialDisposable.set(this)
        compositeDisposable.add(serialDisposable)
        return this
    }

    fun onClear() {
        compositeDisposable.clear()
    }
}