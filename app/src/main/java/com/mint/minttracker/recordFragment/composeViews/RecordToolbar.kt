package com.mint.minttracker.recordFragment.composeViews

import android.widget.Toast
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.mint.minttracker.R
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun RecordToolbar(
    toolbarTitle: String,
    onBackClick:() -> Unit,
) {
    val contextForToast = LocalContext.current.applicationContext

    TopAppBar(
        title = {
            Text(
                text = toolbarTitle,
                color = TrackerTheme.colors.primaryText
            )
        },
        backgroundColor = TrackerTheme.colors.primaryBackground,
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
                Toast.makeText(contextForToast, "Back Icon Click", Toast.LENGTH_SHORT)
                    .show()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                    contentDescription = "Go Back",
                    tint = TrackerTheme.colors.primaryText
                )
            }
        }
    )
}
