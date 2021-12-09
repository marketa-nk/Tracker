package com.mint.minttracker

import android.annotation.SuppressLint
import android.content.Context
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
    fun getLocation(): Observable<MintLocation> {
        return Observable.create { emitter ->
            val request = LocationRequest().also {
                it.interval = 10000
                it.fastestInterval = 5000
                it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProvider.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val location: android.location.Location? = locationResult?.lastLocation
                    if (location != null) {
                        emitter.onNext(MintLocation(location.latitude, location.longitude))
                    }
                }
            }, Looper.getMainLooper())
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(): Single<MintLocation> {
        return Single.create { emitter ->
            fusedLocationProvider.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    emitter.onSuccess(MintLocation(location.latitude, location.longitude))
                }
            }
        }
    }

    companion object {
        lateinit var instance: LocationService
    }

}

