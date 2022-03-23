package com.mint.minttracker.mapFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.SingleLiveEvent
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractor
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractorImpl
import com.mint.minttracker.domain.buttonControl.ButtonState
import com.mint.minttracker.domain.location.LocationInteractor
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
    private val buttonControlInteractor: ButtonControlInteractor,
    private val locationInteractor: LocationInteractor,
    private val tracker: Tracker,
) : BaseViewModel() {

    private var points = mutableListOf<LatLng>()
    val pointsLiveData: MutableLiveData<List<LatLng>> by lazy { MutableLiveData<List<LatLng>>() }
    val lastLocation: MutableLiveData<MintLocation> by lazy { MutableLiveData<MintLocation>() }
    val showHistoryEvent: SingleLiveEvent<String> by lazy { SingleLiveEvent() }
    val buttonState: MutableLiveData<ButtonState> by lazy { MutableLiveData<ButtonState>() }
    val grantedPerm: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val time: MutableLiveData<Long> by lazy { MutableLiveData<Long>() }
    val distance: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }

    private val currentLocationDisposable = SerialDisposable()

    init {
        getPointsForPolyline()
        getDistanse()
        tracker.timeInSec
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                time.value = it
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getDistanse() {
        tracker.distance
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                distance.value = it
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getPointsForPolyline() {
        tracker.location
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                points.add(it.latLng)
                pointsLiveData.value = points
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
            // TODO
        }
    }

    private fun setInitialState() {
        mapInteractor.getLastTrack()
            .flatMap {
                if (it.status != Status.STATUS_FINISHED) {
                    mapInteractor.updateTrack(it.copy(status = Status.STATUS_RESUMED))
                } else {
                    Single.just(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.status != Status.STATUS_FINISHED) {
                    showLastData(it)
                    buttonControlInteractor.start(it.status)
                }
                buttonState.value = buttonControlInteractor.controlButtonPressed(it.status)

                showMyCurrentLocation()
            }, {
                buttonState.value = buttonControlInteractor.controlButtonPressed(Status.STATUS_FINISHED)
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
                pointsLiveData.value = points
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun showMyCurrentLocation() {
        locationInteractor.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ location ->
                lastLocation.value = location
            }, {
                it.printStackTrace()
            })
            .addDisposable(currentLocationDisposable)
    }

    fun controlButtonPressed(status: Status) {
        buttonState.value = buttonControlInteractor.controlButtonPressed(status)
        buttonControlInteractor.start(status)
        if (status == Status.STATUS_STARTED) {
            points.clear()
            pointsLiveData.value = points
        }
    }

    fun historyButtonPressed() {
        showHistoryEvent.value = SHOW_HISTORY_FRAGMENT
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
        const val SHOW_HISTORY_FRAGMENT = "SHOW_HISTORY_FRAGMENT"
    }
}
