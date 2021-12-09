package com.mint.minttracker

import com.arellomobile.mvp.MvpPresenter
import com.mint.minttracker.models.MintLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapPresenter: MvpPresenter<MapView?>() {

    private val compositeDisposable = CompositeDisposable()
    private var location: MintLocation = MintLocation(53.9, 27.5667)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }
    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation{

            }
            // TODO: 12/9/2021  
        } else {
            // TODO: 12/9/2021
        }
    }
    private fun getCurrentLocation(onSuccess: () -> Unit) {
        LocationService.instance.getLastLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                location = it
                onSuccess()
            }, {
                it.printStackTrace()
            })
            .addDisposable()

    }
    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }
}
