package com.mint.minttracker.mapFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.MintLocation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

@InjectViewState
class MapPresenter : MvpPresenter<MapView>() {

    private val polylineOptions = PolylineOptions()
    private val dataBaseRepository: DataBaseRepository = DataBaseRepository()

    private val compositeDisposable = CompositeDisposable()

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.getBundleExtra("Location")
            val lastLocation = bundle?.getParcelable<Parcelable>("Location") as MintLocation?
            if (lastLocation != null) {
                viewState?.showData(lastLocation)
                viewState?.showCurrentLocation(Pair(lastLocation.lat, lastLocation.lon))
                polylineOptions.add(lastLocation.latLng)
                viewState?.drawPolyline(polylineOptions)
            }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
//        if (true) {
//            dataBaseRepository.getLastTrack()
//                .flatMap {id ->
//                    dataBaseRepository.getAllLocationsById(id)
//                        .map { mintList -> mintList.map { it.latLng } }
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSuccess {
//                    it.forEach {latLng -> polylineOptions.add(latLng) }
//                    viewState?.drawPolyline(polylineOptions)
//                }
//                .subscribe()
//                .addDisposable()
//        }
    }

    fun appIsResumed(context: Context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            messageReceiver, IntentFilter("LocationUpdates")
        )
    }

    fun appIsPaused(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver)
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
//            getCurrentLocation()
            // TODO: 12/9/2021
        } else {
            // TODO: 12/9/2021
        }
    }

    fun startButtonPressed(context: Context) {
        viewState?.visibilityStartButton(false)
        startLocationService(context, true)
    }

    fun stopButtonPressed(context: Context) {
        viewState?.visibilityStartButton(true)
        startLocationService(context, false)
    }

    private fun startLocationService(context: Context, run: Boolean) {
        val serviceIntent = Intent(context, LocationServiceForeground::class.java).apply {
            action = if (run) {
                LocationServiceForeground.ACTION_START_FOREGROUND_SERVICE
            } else {
                LocationServiceForeground.ACTION_STOP_FOREGROUND_SERVICE
            }
        }
        ContextCompat.startForegroundService(context, serviceIntent)
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
