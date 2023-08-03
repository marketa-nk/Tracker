package com.mint.minttracker.historyFragment.composeViews

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mint.minttracker.R
import com.mint.minttracker.models.Record
import com.mint.minttracker.msToUiString
import com.mint.minttracker.theme.TrackerTheme
import com.mint.minttracker.toUiString
import java.text.DateFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(
    record: Record,
    isSelected: Boolean,
    onClick: (Record) -> Unit,
    onLongClick: (Record) -> Unit,
) {
    Log.d("LoggingEvent", "HistoryItem ${record.idTrack} - recomposition")
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick(record) },
                onLongClick = { onLongClick(record) }
            )
            .background(
                if (isSelected) TrackerTheme.colors.secondaryBackground else TrackerTheme.colors.primaryBackground
            ),
        verticalArrangement = Arrangement.Top

    ) {
        Text(
            text = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT
            ).format(record.date),
            color = TrackerTheme.colors.primaryText,
            style = TrackerTheme.typography.title,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 16.dp),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            HistorySmallItem(
                drawableResource = R.drawable.ic_baseline_distance_24,
                text = "${(record.distanceKm).toUiString()}км",
                modifier = Modifier.padding(8.dp)
            )
            HistorySmallItem(
                drawableResource = R.drawable.ic_round_timer_24,
                text = record.durationMs.msToUiString(),
                modifier = Modifier.padding(8.dp)
            )
            HistorySmallItem(
                drawableResource = R.drawable.ic_round_speed_24,
                text = "${(record.aveSpeedInKm).toUiString()}км/ч",
                modifier = Modifier.padding(8.dp)
            )
        }
        Divider(startIndent = 0.dp, thickness = 1.dp, color = TrackerTheme.colors.tintColor)
    }
}


@Composable
fun HistorySmallItem(
    drawableResource: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Log.d("LoggingEvent", "HistorySmallItem ${text} - recomposition")
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = "description",
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            text = text,
            modifier = modifier,
            style = MaterialTheme.typography.body1,
            color = Color.White
        )
    }
}
