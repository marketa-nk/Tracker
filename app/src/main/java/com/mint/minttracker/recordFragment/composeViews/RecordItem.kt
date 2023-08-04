package com.mint.minttracker.recordFragment.composeViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.mint.minttracker.R
import com.mint.minttracker.recordFragment.model.RecordParamUi
import com.mint.minttracker.recordFragment.model.RecordUi

@Composable
fun RecordItem(
    record: RecordUi,
    points: List<LatLng>,
    onBackClick: () -> Unit,
) {
    val paramList = mutableListOf(
        RecordParamUi(
            drawableResource = R.drawable.ic_baseline_distance_orange_24,
            textRes = R.string.distance,
            value = record.distance
        ),
        RecordParamUi(
            drawableResource = R.drawable.ic_round_timer_orange_24,
            textRes = R.string.duration,
            value = record.duration
        ),
        RecordParamUi(
            drawableResource = R.drawable.ic_hourglass_24,
            textRes = R.string.total_time_text,
            value = record.totalTime
        ),
        RecordParamUi(
            drawableResource = R.drawable.ic_round_stop_24_orange,
            textRes = R.string.stop_time,
            value = record.stopTime
        ),
        RecordParamUi(
            drawableResource = R.drawable.ic_round_speed_orange_24,
            textRes = R.string.speed_max,
            value = record.speedMax
        ),
        RecordParamUi(
            drawableResource = R.drawable.ic_round_speed_orange_24,
            textRes = R.string.speed_average,
            value = record.speedAve
        )

    )
    Column(verticalArrangement = Arrangement.Top) {
        RecordToolbar(
            toolbarTitle = record.date,
            onBackClick = onBackClick
        )
        RecordMapView(points)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            paramList.forEach { item ->
                item {
                    RecordFieldItem(item)
                }
            }
        }
    }
}

