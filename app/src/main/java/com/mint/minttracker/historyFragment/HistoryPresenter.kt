package com.mint.minttracker.historyFragment

import com.arellomobile.mvp.InjectViewState
import com.mint.minttracker.App
import com.mint.minttracker.BasePresenter
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class HistoryPresenter : BasePresenter<HistoryView>() {

    @Inject
    lateinit var historyInteractor: HistoryInteractor

    init {
        App.instance.appComponent.injectHistoryPresenter(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadRecordList()
    }

    private fun loadRecordList() {
        historyInteractor.loadHistory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("RecordList ${it.size} - nata")
                viewState?.showHistory(it)
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun recordClicked(record: Record) {
        viewState?.showRecordFragment(record)
    }

    fun deleteRecordButtonClicked(record: Record) {
        historyInteractor.deleteRecord(record)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }
}


