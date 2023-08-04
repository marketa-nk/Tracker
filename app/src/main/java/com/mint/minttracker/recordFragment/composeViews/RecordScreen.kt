package com.mint.minttracker.recordFragment.composeViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import com.mint.minttracker.recordFragment.RecordState
import com.mint.minttracker.recordFragment.RecordViewModel

@Composable
fun RecordScreen(viewModel: RecordViewModel) {

    val uiState = viewModel.state.observeAsState()

    when (val state = uiState.value) {
        is RecordState.LoadedState -> RecordItem(
            record = state.record,
            points = state.points,
            onBackClick = {},
        )
        is RecordState.LoadingState -> RecordLoadingView(state.record)
        else -> {}
    }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.showRecord()
    })
}
