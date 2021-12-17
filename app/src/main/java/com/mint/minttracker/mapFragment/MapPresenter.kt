package com.mint.minttracker.mapFragment

import android.graphics.Color
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
import io.reactivex.schedulers.Schedulers
import java.util.*

@InjectViewState
class MapPresenter : MvpPresenter<MapView>() {

    private val dataBaseRepository = DataBaseRepository()

    private var location: MintLocation? = null

    private val polylineOptions = PolylineOptions()

    private val compositeDisposable = CompositeDisposable()

    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation {
                showPolyline()
            }
            // TODO: 12/9/2021  
        } else {
            // TODO: 12/9/2021
        }
    }

    fun startButtonPressed() {

        dataBaseRepository.createTrack()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getLocationUpdates(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
//        getLocationUpdates()
    }

    // вверх
    private val locationService = LocationService.instance

    fun stopButtonPressed() {
        disposable?.dispose()
    }

    private fun showPolyline() {
        viewState?.drawPolyline(polylineOptions)
    }

    private fun getCurrentLocation(onSuccess: () -> Unit) {
        locationService.getLastLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                location = it
                polylineOptions.add(LatLng(it.lat, it.lon))
                    .width(25f)
                    .color(Color.BLUE)
                    .geodesic(true)
                onSuccess()
                viewState?.showData(it)
                viewState?.showCurrentLocation(Pair(it.lat, it.lon))
            }, {
                it.printStackTrace()
            })
            .addDisposable()

    }

    private fun getLocationUpdates(trackId: Long) {

        disposable?.dispose()

        disposable = locationService.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ location ->
                println("getLocationUpdates 1")
                val mintLocation = MintLocation(0, trackId, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
                viewState?.showData(mintLocation)
                viewState?.showCurrentLocation(Pair(mintLocation.lat, mintLocation.lon))
                dataBaseRepository.saveMintLocation(mintLocation)

//                println("${App.instance.database.tracksDao().getCount()} track mintthursday")
//                println("${App.instance.database.mintLocationDao().getCount()} mintlocations mintthursday")
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun Disposable.addDisposable(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
