package com.mint.minttracker.historyFragment.composeViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun HistoryLoadingView() {
    Column(verticalArrangement = Arrangement.Top) {
        HistoryToolbar(
            btnDeleteVisible = false,
            onBackClick = {},
            onDeleteClick = {}
        )
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = TrackerTheme.colors.tintColor
            )
        }
    }
}
