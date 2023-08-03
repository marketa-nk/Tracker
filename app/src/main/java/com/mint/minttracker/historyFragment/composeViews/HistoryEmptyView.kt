package com.mint.minttracker.historyFragment.composeViews

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mint.minttracker.theme.TrackerTheme


@Composable
fun HistoryEmptyView() {
    Log.d("LoggingEvent", "HistoryEmptyView - recomposition")
    Column(verticalArrangement = Arrangement.Top) {
        HistoryToolbar(
            btnDeleteVisible = false,
            onBackClick = {},
            onDeleteClick = {}
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "All your workouts will be here.",
                color = TrackerTheme.colors.primaryText,
                style = TrackerTheme.typography.body
            )
        }
    }
}
