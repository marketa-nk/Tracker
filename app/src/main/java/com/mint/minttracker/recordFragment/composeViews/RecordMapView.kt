package com.mint.minttracker.recordFragment.composeViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun RecordMapView(
    points: List<LatLng>,
) {
    val cameraPositionState = rememberCameraPositionState()
    val uiSettings by remember { mutableStateOf(MapUiSettings()) }
    LaunchedEffect(Unit) {
        val bounds = LatLngBounds.Builder()
            .also { builder ->
                points.forEach { builder.include(it) }
            }.build()
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 64))
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(bottom = 16.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings
    ) {
        Polyline(
            points = points,
            color = TrackerTheme.colors.tintColor,
            width = 15.0F
        )
    }
}
