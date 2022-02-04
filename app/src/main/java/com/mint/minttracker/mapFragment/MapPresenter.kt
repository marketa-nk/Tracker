package com.mint.minttracker.mapFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.mapFragment.LocationServiceForeground.Companion.STATUS
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import com.mint.minttracker.services.LocationService
import com.mint.minttracker.services.Tracker.Companion.LOCATION_BUNDLE
import com.mint.minttracker.services.Tracker.Companion.LOCATION_UPDATES
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AppScope
@InjectViewState
class MapPresenter : BasePresenter<MapView>() {

    @Inject
    lateinit var dataBaseRepository: DataBaseRepository

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var locationService: LocationService

    private var points = mutableListOf<LatLng>()

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.getBundleExtra(LOCATION) //todo "Location" должно быть в статике - done
            val lastLocation = bundle?.getParcelable<Parcelable>(LOCATION_BUNDLE) as MintLocation?

            if (lastLocation != null) {
                showTracking(lastLocation)
            }
        }
    }

    init {
        App.instance.appComponent.injectMapPresenter(this)
    }

    private fun showTracking(lastLocation: MintLocation) {
        viewState?.showData(lastLocation)
        viewState?.showLocation(Pair(lastLocation.lat, lastLocation.lon))
        points.add(lastLocation.latLng)
        viewState.updatePolyline(points)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
        println("onFirstViewAttach Nata")
    }

    fun appIsResumed() {
        getPointsForPolyline()
        LocalBroadcastManager.getInstance(appContext).registerReceiver(
            messageReceiver, IntentFilter(LOCATION_UPDATES)
        )
    }

    private fun getPointsForPolyline() {
        dataBaseRepository.getLastTrack()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it.status != STATUS_FINISHED) {
                    showLastData(it)
                }
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun appIsPaused() {
        //todo переделать
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(messageReceiver)
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            setInitialState()
        } else {
            // TODO: 12/9/2021
        }
    }

    private fun setInitialState() {
        dataBaseRepository.getLastTrack()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSuccess {
                if (it.status != STATUS_FINISHED) {
                    dataBaseRepository.updateTrack(Track(it.id, it.date, STATUS_PAUSED))
                    stateOfButtons(start = false, pause = false, resume = true, stop = true)
                    showLastData(it)
                } else {
                    stateOfButtons(start = true, pause = false, resume = false, stop = false)
                    showMyCurrentLocation()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                stateOfButtons(start = true, pause = false, resume = false, stop = false)
                showMyCurrentLocation()
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun showLastData(track: Track) {
        dataBaseRepository.getAllLocationsById(track.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mintLocations ->
                if (mintLocations.size > 1) {
                    viewState?.showData(mintLocations[mintLocations.size - 1])
                    viewState?.showLocation(Pair(mintLocations[mintLocations.size - 1].lat, mintLocations[mintLocations.size - 1].lon))
                    points.addAll(mintLocations.map { it.latLng })
                    viewState?.updatePolyline(points)
                }
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    //todo название метода не соотвествует коду - done
    private fun showMyCurrentLocation() {
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

    fun startButtonPressed() {
        points = mutableListOf()
        viewState?.updatePolyline(points)
        stateOfButtons(start = false, pause = true, resume = false, stop = true)
        startLocationService(true, STATUS_STARTED)
    }

    fun pauseButtonPressed() {
        stateOfButtons(start = false, pause = false, resume = true, stop = true)
        startLocationService(false, STATUS_PAUSED)
    }

    fun resumeButtonPressed() {
        stateOfButtons(start = false, pause = true, resume = false, stop = true)
        startLocationService(true, STATUS_RESUMED)
    }

    fun stopButtonPressed() {
        stateOfButtons(start = true, pause = false, resume = false, stop = false)
        startLocationService(false, STATUS_FINISHED)
    }

    private fun stateOfButtons(start: Boolean, pause: Boolean, resume: Boolean, stop: Boolean) {
        viewState?.visibilityStartButton(start)
        viewState?.visibilityPauseButton(pause)
        viewState?.visibilityResumeButton(resume)
        viewState?.visibilityStopButton(stop)
    }

    fun historyButtonPressed() {
        viewState?.navigateToHistoryFragment()
    }

    private fun startLocationService(run: Boolean, status: String) {
        val serviceIntent = Intent(appContext, LocationServiceForeground::class.java).apply {
            putExtra(STATUS, status)
            action = if (run) {
                LocationServiceForeground.ACTION_START_FOREGROUND_SERVICE
            } else {
                LocationServiceForeground.ACTION_STOP_FOREGROUND_SERVICE
            }
        }
        ContextCompat.startForegroundService(appContext, serviceIntent)
    }

    companion object {
        const val STATUS_STARTED = "STATUS_STARTED"
        const val STATUS_PAUSED = "STATUS_PAUSED"
        const val STATUS_RESUMED = "STATUS_RESUMED"
        const val STATUS_FINISHED = "STATUS_FINISHED"
        const val LOCATION = "LOCATION"
    }
}
