package com.mint.minttracker.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
internal fun MainTheme(
    content: @Composable () -> Unit
) {

    val colors = basePalette

    val typography = TrackerTypography(
        heading = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        body = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        ),
        toolbar = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        caption = TextStyle(
            fontSize = 12.sp
        ),
        title = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    )

    CompositionLocalProvider(
        LocalTrackerColors provides colors,
        LocalTrackerTypography provides typography,
        content = content
    )
}