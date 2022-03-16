package com.mint.minttracker.historyFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.SingleLiveEvent
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val historyInteractor: HistoryInteractor) : BaseViewModel() {

    val records: MutableLiveData<List<Record>> = MutableLiveData<List<Record>>()
    val displayRecordScreenEvent: SingleLiveEvent<Record> = SingleLiveEvent()
    val messageEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val listIsEmpty: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        loadRecordList()
    }

    private fun loadRecordList() {
        historyInteractor.loadHistory()
            .map { it.reversed() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("RecordList ${it.size} - nata")
                records.value = it
                listIsEmpty.value = it.isEmpty()
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun recordClicked(rec: Record) {
        this.displayRecordScreenEvent.value = rec
    }

    fun deleteRecordButtonClicked(record: Record) {
        historyInteractor.deleteRecord(record)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                messageEvent.value = "Track was deleted."
            }, {
                messageEvent.value = "Error deleting"
                it.printStackTrace()
            })
            .addDisposable()
    }

    class HistoryViewModelFactory @Inject constructor(
        private val historyInteractor: HistoryInteractor,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            require(modelClass == HistoryViewModel::class.java)
            return HistoryViewModel(historyInteractor) as T
        }
    }
}


