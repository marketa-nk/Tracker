package com.mint.minttracker.historyFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mint.minttracker.models.Record

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface HistoryView: MvpView{

    fun showHistory(records: List<Record>)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showRecordFragment(record: Record)
}
