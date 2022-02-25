package com.mint.minttracker.mapFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractorImpl
import com.mint.minttracker.domain.buttonControl.ButtonState
import com.mint.minttracker.domain.location.LocationInteractorImpl
import com.mint.minttracker.domain.map.MapInteractor
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Status
import com.mint.minttracker.models.Track
import com.mint.minttracker.services.Tracker
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapViewModel(
    private val mapInteractor: MapInteractor,
    private val buttonControlInteractorImpl: ButtonControlInteractorImpl,
    private val locationInteractorImpl: LocationInteractorImpl,
    private val tracker: Tracker,
) : BaseViewModel() {

    private var points = mutableListOf<LatLng>()
    val pointsLiveData: MutableLiveData<List<LatLng>> by lazy { MutableLiveData<List<LatLng>>() }
    val mintLocation: MutableLiveData<MintLocation> by lazy { MutableLiveData<MintLocation>() }
    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }//todo refactoring
    val buttonState: MutableLiveData<ButtonState> by lazy { MutableLiveData<ButtonState>() }
    val grantedPerm: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }

    private val currentLocationDisposable = SerialDisposable()

    init {
//        viewState?.requireLocationPermission()
        getPointsForPolyline()
    }

    private fun getPointsForPolyline() {
        tracker.location
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                points.add(it.latLng)
                pointsLiveData.value = points
//                viewState?.updatePolyline(points)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            grantedPerm.value = true
            setInitialState()
        } else {
            // TODO: 12/9/2021
        }
    }

    private fun setInitialState() {
        mapInteractor.getLastTrack()
            .flatMap {
                if (it.status != Status.STATUS_FINISHED) {
                    mapInteractor.updateTrack(it.copy(status = Status.STATUS_PAUSED))
                } else {
                    Single.just(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.status != Status.STATUS_FINISHED) {
                    showLastData(it)
                }
                buttonState.value = buttonControlInteractorImpl.controlButtonPressed(it.status)
                showMyCurrentLocation()
            }, {
                buttonState.value = buttonControlInteractorImpl.controlButtonPressed(Status.STATUS_FINISHED)
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
//                viewState?.updatePolyline(points)
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
                mintLocation.value = lastLocation
//                viewState?.showData(lastLocation)
//                viewState?.moveCamera(lastLocation.latLng)
            }, {
                it.printStackTrace()
            })
            .addDisposable(currentLocationDisposable)
    }

    fun controlButtonPressed(status: Status) {
        buttonState.value = buttonControlInteractorImpl.controlButtonPressed(status)
        buttonControlInteractorImpl.start(status)
        if (status == Status.STATUS_STARTED) {
            points.clear()
        //            viewState?.updatePolyline(points)
        }
    }

    fun historyButtonPressed() {
        message.value = SHOW_HISTORY_FRAGMENT
//        viewState?.navigateToHistoryFragment()
    }

    fun myLocationButtonIsClicked() {
        showMyCurrentLocation()
    }

    fun cameraIsMovedByGesture() {
        currentLocationDisposable.set(null)
    }

    class MapViewModelFactory @Inject constructor(
        private val mapInteractor: MapInteractor,
        private val buttonControlInteractorImpl: ButtonControlInteractorImpl,
        private val locationInteractorImpl: LocationInteractorImpl,
        private val tracker: Tracker,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            require(modelClass == MapViewModel::class.java)
            return MapViewModel(mapInteractor, buttonControlInteractorImpl, locationInteractorImpl, tracker) as T
        }
    }

    companion object {
//        const val STATUS_STARTED = "STATUS_STARTED"
//        const val STATUS_PAUSED = "STATUS_PAUSED"
//        const val STATUS_RESUMED = "STATUS_RESUMED"
//        const val STATUS_FINISHED = "STATUS_FINISHED"
        const val SHOW_HISTORY_FRAGMENT = "SHOW_HISTORY_FRAGMENT"
    }
}
