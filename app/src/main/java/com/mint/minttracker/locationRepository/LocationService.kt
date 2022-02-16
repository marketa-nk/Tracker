package com.mint.minttracker.locationRepository

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
import javax.inject.Inject

class LocationService @Inject constructor(context: Context) : LocationRepository {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocation(): Observable<Location> {
        return Observable.create { emitter ->
            val request = LocationRequest.create().apply {
                interval = 100
                fastestInterval = 50
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    println("getLocationUpdates locationResult - nata")
                    val location: Location? = locationResult.lastLocation
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
    override fun getLastLocation(): Single<MintLocation> {
        return Single.create { emitter ->
            try {
                fusedLocationProvider.lastLocation.addOnSuccessListener { location: Location? ->
                    location ?: emitter.onError(NullPointerException("LastLocation is null"))
                    if (location != null) {
                        emitter.onSuccess(MintLocation(0, -1, location.time, location.latitude, location.longitude, location.altitude, location.speed, location.bearing, location.accuracy))
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}

