package com.mint.minttracker.recordFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.models.Record

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface RecordView : MvpView {

    //    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showRecordInfo(record: Record)
    fun showPolyline(list: List<LatLng>)
}
