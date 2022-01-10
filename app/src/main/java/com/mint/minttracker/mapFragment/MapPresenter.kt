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
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class MapPresenter : MvpPresenter<MapView>() {

    private var points = mutableListOf<LatLng>()
    private val dataBaseRepository: DataBaseRepository = DataBaseRepository()
    private val locationService: LocationService = LocationService.instance

    private val compositeDisposable = CompositeDisposable()

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.getBundleExtra("Location")
            val lastLocation = bundle?.getParcelable<Parcelable>("Location") as MintLocation?
            if (lastLocation != null) {
                viewState?.showData(lastLocation)
                viewState?.showLocation(Pair(lastLocation.lat, lastLocation.lon))
                points.add(lastLocation.latLng)
                viewState.updatePolyline(points)
            }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
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
            dataBaseRepository.getLastTrack()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess {
                    if (it != null && it.status != STATUS_FINISHED) {
                        println("${it.status} before Nata")
                        dataBaseRepository.updateTrack(Track(it.id, it.date, STATUS_PAUSED))
                        stateOfButtons(start = false, pause = false, resume = true, stop = true)
                        showLastData(it)
                    } else {
                        stateOfButtons(start = true, pause = false, resume = false, stop = false)
                        getMyCurrentLocation()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    it.printStackTrace()
                }).addDisposable()
        } else {
            // TODO: 12/9/2021
        }
    }

    private fun showLastData(track: Track) {
        dataBaseRepository.getAllLocationsById(track.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mintLocations ->
                viewState?.showData(mintLocations[mintLocations.size - 1])
                viewState?.showLocation(Pair(mintLocations[mintLocations.size - 1].lat, mintLocations[mintLocations.size - 1].lon))
                points.addAll(mintLocations.map { it.latLng })
                viewState?.updatePolyline(points)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getMyCurrentLocation() {
        locationService.getLocation()
            .observeOn(Schedulers.io())
            .map { location ->
                MintLocation(0, 0, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lastLocation ->
                viewState?.showData(lastLocation)
                viewState?.showLocation(Pair(lastLocation.lat, lastLocation.lon))
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun startButtonPressed(context: Context) {
        points = mutableListOf()
        viewState?.updatePolyline(points)
        stateOfButtons(start = false, pause = true, resume = false, stop = true)
        startLocationService(context, true, STATUS_STARTED)
    }

    fun pauseButtonPressed(context: Context) {
        stateOfButtons(start = false, pause = false, resume = true, stop = true)
        startLocationService(context, false, STATUS_PAUSED)
    }

    fun resumeButtonPressed(context: Context) {
        stateOfButtons(start = false, pause = true, resume = false, stop = true)
        startLocationService(context, true, STATUS_RESUMED)
    }

    fun stopButtonPressed(context: Context) {
        stateOfButtons(start = true, pause = false, resume = false, stop = false)
        startLocationService(context, false, STATUS_FINISHED)
    }

    private fun stateOfButtons (start: Boolean, pause: Boolean, resume: Boolean, stop: Boolean){
        viewState?.visibilityStartButton(start)
        viewState?.visibilityPauseButton(pause)
        viewState?.visibilityResumeButton(resume)
        viewState?.visibilityStopButton(stop)
    }

    private fun startLocationService(context: Context, run: Boolean, status: String) {
        val serviceIntent = Intent(context, LocationServiceForeground::class.java).apply {
            putExtra("status", status)
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

    companion object {
        const val STATUS_STARTED = "STATUS_STARTED"
        const val STATUS_PAUSED = "STATUS_PAUSED"
        const val STATUS_RESUMED = "STATUS_RESUMED"
        const val STATUS_FINISHED = "STATUS_FINISHED"
    }
}
