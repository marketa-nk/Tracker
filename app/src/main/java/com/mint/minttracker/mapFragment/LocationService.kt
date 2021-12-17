package com.mint.minttracker.mapFragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mint.minttracker.models.MintLocation
import io.reactivex.Observable
import io.reactivex.Single

class LocationService(context: Context) {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocation(): Observable<Location> {
        return Observable.create { emitter ->
            val request = LocationRequest.create().apply {
                interval = 100
                fastestInterval = 50
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    println("getLocationUpdates locationResult")
                    val location: android.location.Location? = locationResult.lastLocation
                    if (location != null) {
                        emitter.onNext(location)
                    }
                }
            }
            fusedLocationProvider.requestLocationUpdates(request, callback, Looper.getMainLooper())

            emitter.setCancellable {
                fusedLocationProvider.removeLocationUpdates(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(): Single<MintLocation> {
        return Single.create { emitter ->
            fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
                emitter.onSuccess(MintLocation(0, -1, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy))
            }
        }
    }

    companion object {
        lateinit var instance: LocationService
    }

}

