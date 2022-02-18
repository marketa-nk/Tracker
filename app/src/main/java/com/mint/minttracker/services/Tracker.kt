package com.mint.minttracker.services

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.domain.location.LocationInteractorImpl
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_FINISHED
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_RESUMED
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_STARTED
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@AppScope
class Tracker @Inject constructor(
    private val dataBaseRepository: DataBaseRepository,
    private val locationInteractorImpl: LocationInteractorImpl,
) {


    private val disposableLocationUpdates = SerialDisposable()

    private val compositeDisposable = CompositeDisposable()
    private var locationPublishSubject = PublishSubject.create<MintLocation>()
    var location: Observable<MintLocation> = locationPublishSubject.hide()

    fun start(status: String) {
        when (status) {
            STATUS_STARTED ->
                dataBaseRepository.createTrack()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        saveLocationUpdates(it)
                    }, {
                        it.printStackTrace()
                    })
                    .addDisposable()
            STATUS_RESUMED ->
                dataBaseRepository.getLastTrack()
                    .flatMap { track ->
                        dataBaseRepository.updateTrack(track.copy(status = status))
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        saveLocationUpdates(it.id)
                    }, {
                        it.printStackTrace()
                    })
                    .addDisposable()
        }
    }

    fun stop(status: String) {
        disposableLocationUpdates.set(null)
        dataBaseRepository.getLastTrack()
            .flatMap { track ->
                dataBaseRepository.getAllLocationsById(track.id)
                    .map { track to it }
            }
            .flatMap { (track, locations) ->
                if (locations.isEmpty() && status == STATUS_FINISHED) {
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

    private fun saveLocationUpdates(trackId: Long) {
        locationInteractorImpl.getLocation()
            .concatMapSingle { location ->
                dataBaseRepository.saveLocation(location, trackId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                locationPublishSubject.onNext(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable(disposableLocationUpdates)
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

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}