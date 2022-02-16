package com.mint.minttracker.mapFragment

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractorImpl
import com.mint.minttracker.domain.buttonControl.ButtonState
import com.mint.minttracker.domain.location.LocationInteractorImpl
import com.mint.minttracker.domain.map.MapInteractor
import com.mint.minttracker.models.Track
import com.mint.minttracker.services.Tracker
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class MapPresenter : BasePresenter<MapView>() {

    @Inject
    lateinit var mapInteractor: MapInteractor

    @Inject
    lateinit var buttonControlInteractorImpl: ButtonControlInteractorImpl

    @Inject
    lateinit var locationInteractorImpl: LocationInteractorImpl

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var tracker: Tracker

    private var points = mutableListOf<LatLng>()
    private val currentLocationDisposable = SerialDisposable()

    init {
        App.instance.appComponent.injectMapPresenter(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
        getPointsForPolyline()
        println("onFirstViewAttach Nata")
    }

    private fun getPointsForPolyline() {
        tracker.location
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                points.add(it.latLng)
                viewState?.updatePolyline(points)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            setInitialState()
        } else {
            // TODO: 12/9/2021
        }
    }

    private fun setInitialState() {
        mapInteractor.getLastTrack()
            .flatMap {
                if (it.status != STATUS_FINISHED) {
                    mapInteractor.updateTrack(it.copy(status = STATUS_PAUSED))
                } else {
                    Single.just(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.status != STATUS_FINISHED) {
                    showLastData(it)
                }
                stateOfButtons(buttonControlInteractorImpl.controlButtonPressed(it.status))
                showMyCurrentLocation()
            }, {
                stateOfButtons(buttonControlInteractorImpl.controlButtonPressed(STATUS_FINISHED))
                showMyCurrentLocation()
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun showLastData(track: Track) {
        mapInteractor.getAllLocationsById(track.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mintLocations ->
                points.clear()
                points.addAll(mintLocations.map { it.latLng })
                viewState?.updatePolyline(points)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun showMyCurrentLocation() {
         locationInteractorImpl.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lastLocation ->
                viewState?.showData(lastLocation)
                viewState?.moveCamera(lastLocation.latLng)
            }, {
                it.printStackTrace()
            })
            .addDisposable(currentLocationDisposable)
    }

    fun controlButtonPressed(str: String) {
        stateOfButtons(buttonControlInteractorImpl.controlButtonPressed(str))
        buttonControlInteractorImpl.start(str)
        if (str == STATUS_STARTED) {
            points = mutableListOf()
            viewState?.updatePolyline(points)
        }
    }

    private fun stateOfButtons(buttonState: ButtonState) {
        viewState?.visibilityStartButton(buttonState.start)
        viewState?.visibilityPauseButton(buttonState.pause)
        viewState?.visibilityResumeButton(buttonState.resume)
        viewState?.visibilityStopButton(buttonState.stop)
    }

    fun historyButtonPressed() {
        viewState?.navigateToHistoryFragment()
    }

    fun myLocationButtonIsClicked() {
        showMyCurrentLocation()
    }

    fun cameraIsMovedByGesture() {
        currentLocationDisposable.set(null)
    }

    companion object {
        const val STATUS_STARTED = "STATUS_STARTED"
        const val STATUS_PAUSED = "STATUS_PAUSED"
        const val STATUS_RESUMED = "STATUS_RESUMED"
        const val STATUS_FINISHED = "STATUS_FINISHED"
    }
}
