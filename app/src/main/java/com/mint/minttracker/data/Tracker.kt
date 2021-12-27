package com.mint.minttracker.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mint.minttracker.App
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.mapFragment.LocationService
import com.mint.minttracker.models.MintLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers


class Tracker(
    private val context: Context,
    private val dataBaseRepository: DataBaseRepository = DataBaseRepository(),
    private val locationService: LocationService = LocationService.instance
) {

    val compositeDisposable = CompositeDisposable()
    private val disposableLocationUpdates = SerialDisposable()
    private val disposableStartTracking = SerialDisposable()

    fun start() {
        dataBaseRepository.createTrack()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getLocationUpdates(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun stop() {
//        disposableStartTracking.set(null)
        disposableLocationUpdates.set(null)
    }



    private fun getLocationUpdates(trackId: Long) {
        locationService.getLocation()
            .observeOn(Schedulers.io())
            .map { location ->
                MintLocation(0, trackId, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
            }
            .concatMapSingle { location ->
                dataBaseRepository.saveLocation(location)
            }
            .doOnNext {

                println("${App.instance.database.tracksDao().getCount()} tracks Nata")
                println("${App.instance.database.mintLocationDao().getCount()} mintlocations Nata")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                sendMessageToFragment(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable(disposableLocationUpdates)
    }

    private fun sendMessageToFragment(mintLocation: MintLocation) {
        val intent = Intent("LocationUpdates")
        val bundle = Bundle()
        bundle.putParcelable("Location", mintLocation)
        intent.putExtra("Location", bundle)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
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
}