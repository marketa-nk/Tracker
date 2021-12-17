package com.mint.minttracker.mapFragment

import android.location.Location
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.models.MintLocation

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MapView: MvpView{

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun requireLocationPermission()
    fun drawPolyline(polylineOptions: PolylineOptions)
    fun showData(mintLocation: MintLocation)
    fun showCurrentLocation(location: Pair<Double, Double>)


}
