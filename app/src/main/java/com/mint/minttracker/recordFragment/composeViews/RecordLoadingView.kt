package com.mint.minttracker.recordFragment.composeViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mint.minttracker.recordFragment.model.RecordUi
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun RecordLoadingView(record: RecordUi) {
    Column(verticalArrangement = Arrangement.Top) {
        RecordToolbar(
            toolbarTitle = record.date,
            onBackClick = {},
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
