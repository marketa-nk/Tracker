package com.mint.minttracker.recordFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.models.Record
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class RecordViewModelFactory @AssistedInject constructor(
    @Assisted("record")
    private val record: Record,
    private val recordInteractor: RecordInteractor,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == RecordViewModel::class.java)
        return RecordViewModel(record, recordInteractor) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("record") record: Record): RecordViewModelFactory
    }
}