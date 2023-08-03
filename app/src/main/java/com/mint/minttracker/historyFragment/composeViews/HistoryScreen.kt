package com.mint.minttracker.historyFragment.composeViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.mint.minttracker.R
import com.mint.minttracker.historyFragment.HistoryState
import com.mint.minttracker.historyFragment.HistoryViewModel
import com.mint.minttracker.models.Record
import com.mint.minttracker.recordFragment.RecordFragment

@Composable
fun HistoryScreen(viewModel: HistoryViewModel, navController: NavController) {

    val uiState = viewModel.state.observeAsState()
    val onRecordClick: (Record) -> Unit = remember {
        { record ->
            navController.navigate(
                R.id.action_historyFragment_to_recordFragment,
                bundleOf(RecordFragment.ARG_RECORD to record)
            )
        }
    }
    val onRecordLongClick: (Record) -> Unit = remember { { record -> viewModel.recordLongClicked(record) } }
    val onDeleteClick: () -> Unit = remember { { viewModel.deleteRecord() } }

    when (val state = uiState.value) {
        is HistoryState.LoadedHistoryState -> HistoryListView(
            records = state.records,
            selectedRecord = state.selectedRecord,
            showDeleteBtn = state.btnDeleteVisible,
            onClick = onRecordClick,
            onLongClick = onRecordLongClick,
            onDeleteClick = onDeleteClick
        )
        is HistoryState.NoItemsHistoryState -> HistoryEmptyView()
        is HistoryState.LoadingHistoryState -> HistoryLoadingView()
        is HistoryState.ErrorHistoryState -> HistoryEmptyView()
        else -> {}
    }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadRecordList()
    })
}
