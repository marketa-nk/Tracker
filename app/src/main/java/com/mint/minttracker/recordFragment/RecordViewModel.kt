package com.mint.minttracker.recordFragment

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.BaseViewModel
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.extensions.set
import com.mint.minttracker.models.Record
import com.mint.minttracker.recordFragment.mapper.RecordUiMapper
import com.mint.minttracker.recordFragment.model.RecordUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecordViewModel @AssistedInject constructor(
    @Assisted private val record: Record,
    private val recordInteractor: RecordInteractor,
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(record: Record): RecordViewModel
    }

    val state: MutableLiveData<RecordState> = MutableLiveData<RecordState>(
        RecordState.LoadingState(RecordUiMapper().invoke(record))
    )

    fun showRecord() {
        val recordUi = RecordUiMapper().invoke(record)
        state.set(newValue = RecordState.LoadingState(recordUi))
        recordInteractor.getListLocationByTrackId(record.idTrack)
            .map {
                it.map { mintLocation -> mintLocation.latLng }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ points ->
                state.set(
                    newValue = RecordState.LoadedState(
                        record = recordUi,
                        points = points,
                    )
                )

            }, {
                state.set(
                    newValue = RecordState.LoadedState(
                        record = recordUi,
                        points = emptyList(),
                    )
                )
                it.printStackTrace()
            })
            .addDisposable()
    }
}

sealed class RecordState {
    class LoadingState(
        val record: RecordUi
    ) : RecordState()
    class LoadedState(
        val record: RecordUi,
        val points: List<LatLng>,
    ) : RecordState()
    class ErrorState(val message: String) : RecordState()
}

