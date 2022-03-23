package com.mint.minttracker.services

import com.mint.minttracker.domain.history.LocationUtils.distanceToMintlocationInM
import com.mint.minttracker.models.MintLocation
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class DistanceCalculator @Inject constructor() {

    var distancePublishSubject: Subject<Double> = PublishSubject.create()

    private var lastLocationUsedForDistanceTraveled: MintLocation? = null
    private var distanceTraveled: Double = 0.0
    private var distanceTraveledInCurrentSegment: Double = 0.0
    private var segment: Int = -1

    fun calculate(currentLocation: MintLocation) {
        if (lastLocationUsedForDistanceTraveled == null || segment != currentLocation.segment) {
            lastLocationUsedForDistanceTraveled = currentLocation
            distanceTraveled += distanceTraveledInCurrentSegment
            distanceTraveledInCurrentSegment = 0.0
            distancePublishSubject.onNext(distanceTraveled + distanceTraveledInCurrentSegment)
            segment = currentLocation.segment
        } else {
            val distanceFromPreviousLocationInM = currentLocation.distanceToMintlocationInM(lastLocationUsedForDistanceTraveled!!)
            distanceTraveledInCurrentSegment += distanceFromPreviousLocationInM
            distancePublishSubject.onNext(distanceTraveled + distanceTraveledInCurrentSegment)
            lastLocationUsedForDistanceTraveled = currentLocation
        }
    }

    fun setStartState(restoredDistance: Double) {
        distanceTraveled = restoredDistance
        distanceTraveledInCurrentSegment = 0.0
        segment = -1
    }
}