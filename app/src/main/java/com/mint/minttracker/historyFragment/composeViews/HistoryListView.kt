package com.mint.minttracker.historyFragment.composeViews

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mint.minttracker.models.Record

@Composable
fun HistoryListView(
    records: List<Record>,
    selectedRecord: Record?,
    showDeleteBtn: Boolean,
    onClick: (Record) -> Unit,
    onLongClick: (Record) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Log.d("LoggingEvent", "HistoryListView size ${records.size} - recomposition")
    Column(verticalArrangement = Arrangement.Top) {
        HistoryToolbar(
            btnDeleteVisible = showDeleteBtn,
            onBackClick = {},
            onDeleteClick = { onDeleteClick() }
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            records.forEach { record ->
                item {
                    HistoryItem(
                        record = record,
                        isSelected = record == selectedRecord,
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                }
            }
        }
    }
}
