package com.mint.minttracker.historyFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.models.Record
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val historyInteractor: HistoryInteractor) : BaseViewModel() {

    val records: MutableLiveData<List<Record>> by lazy { MutableLiveData<List<Record>>() }
    val recordData: MutableLiveData<Record> by lazy { MutableLiveData<Record>() } //todo what is recordData&?
    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }//todo fix

    init {
        message.value = "dsfdkls"
        loadRecordList()
    }

    private fun loadRecordList() {
        historyInteractor.loadHistory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("RecordList ${it.size} - nata")
                records.value = it
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun recordClicked(record: Record) {
        recordData.value = record
    }

    fun deleteRecordButtonClicked(record: Record) {
        historyInteractor.deleteRecord(record)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                message.value = "Track was deleted."
            }, {
                message.value = "Error deleting"
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


