package com.mint.minttracker.domain.history

import android.location.Location
import com.mint.minttracker.getTotalTimeInMillis
import com.mint.minttracker.models.MintLocation

object LocationUtils {

    fun calcDistanceMeters(list: List<MintLocation>): Double {
        return getSegmentWithLocationlistMap(list).sumOf {
            calcDistanceInMeters(it)
        }
    }

    private fun getSegmentWithLocationlistMap(list: List<MintLocation>): Collection<List<Location>> {
        return list.groupBy({ mintLocation -> mintLocation.segment }, { mintLocation ->
            Location("loc").also { location ->
                location.latitude = mintLocation.lat
                location.longitude = mintLocation.lon
            }
        }).values
    }

    private fun calcDistanceInMeters(list: List<Location>): Double {
        var distance = 0.0
        list.forEachIndexed { i, it ->
            val location = list.getOrNull(i + 1) ?: return@forEachIndexed
            distance += it.distanceTo(location)
        }
        return distance
    }

    fun calcAverageSpeed(list: List<MintLocation>): Double{
        return calcDistanceMeters(list) / 1000 / list.getTotalTimeInMillis() * 3600000
    }

    fun MintLocation.distanceToMintlocationInM(mintLocation: MintLocation): Double {
        return Location("loc").also { location1 ->
            location1.latitude = this.lat
            location1.longitude = this.lon
        }.distanceTo(
            Location("loc").also { location2 ->
                location2.latitude = mintLocation.lat
                location2.longitude = mintLocation.lon
            }).toDouble()
    }
}