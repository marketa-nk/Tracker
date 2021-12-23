package com.mint.minttracker.mapFragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.App
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.MintLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers


@InjectViewState
class MapPresenter : MvpPresenter<MapView>() {

    private val dataBaseRepository = DataBaseRepository()

    private var location: MintLocation? = null

    private val polylineOptions = PolylineOptions()

    private val compositeDisposable = CompositeDisposable()
    private val serialDisposable = SerialDisposable()

    private val locationService = LocationService.instance

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation()
            // TODO: 12/9/2021  
        } else {
            // TODO: 12/9/2021
        }
    }

    fun startButtonPressed(context: Context) {
        startLocationService(context, true) //todo потом обсудим
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

    fun stopButtonPressed(context: Context) {
        startLocationService(context, false)
        serialDisposable.set(null)
    }

    private fun startLocationService(context: Context, run: Boolean) {
        val serviceIntent = Intent(context, LocationServiceForeground::class.java)
        when (run) {
            true -> serviceIntent.action = LocationServiceForeground.ACTION_START_FOREGROUND_SERVICE
            else -> serviceIntent.action = LocationServiceForeground.ACTION_STOP_FOREGROUND_SERVICE
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun showPolyline() {
//        viewState?.drawPolyline(polylineOptions)
    }

    private fun getCurrentLocation() {
        locationService.getLastLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                location = it
                polylineOptions.add(LatLng(it.lat, it.lon))
                    .width(25f)
                    .color(Color.BLUE)
                    .geodesic(true)
                showPolyline()
                viewState?.showData(it)
                viewState?.showCurrentLocation(Pair(it.lat, it.lon))
            }, {
                it.printStackTrace()
            })
            .addDisposable()

    }

    private fun getLocationUpdates(trackId: Long) {
        locationService.getLocation()
            .map { location ->
                MintLocation(0, trackId, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
            }
            .doOnNext {
                dataBaseRepository.saveMintLocation(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mintLocation ->

                viewState?.showData(mintLocation)
                viewState?.showCurrentLocation(Pair(mintLocation.lat, mintLocation.lon))

                //todo App.instance.database.tracksDao() вынести в поле
                //todo App.instance.database.mintLocationDao() вынести в поле
                println("${App.instance.database.tracksDao().getCount()} tracks Nata")
                println("${App.instance.database.mintLocationDao().getCount()} mintlocations Nata")
            }, {
                it.printStackTrace()
            })
            .addDisposable(serialDisposable)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
