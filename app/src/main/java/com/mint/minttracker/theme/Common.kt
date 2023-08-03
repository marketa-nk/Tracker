package com.mint.minttracker.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TrackerColors (
    val primaryText: Color,
    val primaryBackground: Color,
//    val secondaryText: Color,
    val secondaryBackground: Color,
    val tintColor: Color,
    val controlColor: Color,
//    val errorColor: Color,
)

data class TrackerTypography(
    val heading: TextStyle,
    val body: TextStyle,
    val toolbar: TextStyle,
    val caption: TextStyle,
    val title: TextStyle,
)

data class TrackerImage(
    val mainIcon: Int?,
    val mainIconDescription: String
)

object TrackerTheme {
    internal val colors: TrackerColors
        @Composable
        internal get() = LocalTrackerColors.current

    internal val typography: TrackerTypography
        @Composable
        internal get() = LocalTrackerTypography.current
}

internal val LocalTrackerColors = staticCompositionLocalOf<TrackerColors> {
    error("No colors provided")
}

internal val LocalTrackerTypography = staticCompositionLocalOf<TrackerTypography> {
    error("No font provided")
}
internal val LocalTrackerImage = staticCompositionLocalOf<TrackerImage> {
    error("No images provided")
}