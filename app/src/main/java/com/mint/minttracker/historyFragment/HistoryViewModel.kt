package com.mint.minttracker.historyFragment

import androidx.lifecycle.MutableLiveData
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.extensions.set
import com.mint.minttracker.models.Record
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HistoryViewModel @AssistedInject constructor(private val historyInteractor: HistoryInteractor) : BaseViewModel() {

    val state: MutableLiveData<HistoryState> = MutableLiveData<HistoryState>(HistoryState.LoadingHistoryState())

    fun loadRecordList() {
        state.set(newValue = HistoryState.LoadingHistoryState())
        historyInteractor.loadHistory()
            .map { it.reversed() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ recordList ->
                if (recordList.isEmpty()) {
                    state.set(newValue = HistoryState.NoItemsHistoryState())
                } else {
                    state.set(
                        newValue = HistoryState.LoadedHistoryState(
                            records = recordList,
                            selectedRecord = null,
                            btnDeleteVisible = false
                        )
                    )
                }
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    fun recordLongClicked(rec: Record) {
        val selectedRecord = (state.value as HistoryState.LoadedHistoryState).selectedRecord
        val newSelectedRecord = if (rec != selectedRecord) rec else null
        state.set(
            newValue = HistoryState.LoadedHistoryState(
                records = (state.value as HistoryState.LoadedHistoryState).records,
                selectedRecord = newSelectedRecord,
                btnDeleteVisible = newSelectedRecord != null
            )
        )
    }

    fun deleteRecord() {
        val selectedRecord = (state.value as HistoryState.LoadedHistoryState).selectedRecord
        selectedRecord?.let { deleteRecordButtonClicked(selectedRecord) }
        state.set(
            newValue = HistoryState.LoadedHistoryState(
                records = (state.value as HistoryState.LoadedHistoryState).records,
                selectedRecord = selectedRecord,
                btnDeleteVisible = false
            )
        )
    }

    private fun deleteRecordButtonClicked(record: Record) {
        historyInteractor.deleteRecord(record)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                state.set(
                    newValue = HistoryState.LoadedHistoryState(
                        records = (state.value as HistoryState.LoadedHistoryState).records
                            .filter { it.idTrack != record.idTrack },
                        selectedRecord = null,
                        btnDeleteVisible = false
                    )
                )
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    @AssistedFactory
    interface HistoryViewModelFactory {
        fun create(): HistoryViewModel
    }
}

sealed class HistoryState {
    class LoadingHistoryState : HistoryState()
    class LoadedHistoryState(
        val records: List<Record> = emptyList(),
        val selectedRecord: Record? = null,
        val btnDeleteVisible: Boolean = false,
    ) : HistoryState()

    class NoItemsHistoryState : HistoryState()
    class ErrorHistoryState(val message: String) : HistoryState()
}
