package com.mint.minttracker.historyFragment.composeViews

import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mint.minttracker.R
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun HistoryToolbar(
    btnDeleteVisible: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: (() -> Unit),
) {
    val contextForToast = LocalContext.current.applicationContext

    TopAppBar(
        title = {
            Text(
                text = "History",
                color = TrackerTheme.colors.primaryText
            )
        },
        backgroundColor = TrackerTheme.colors.primaryBackground,
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(contextForToast, "Back Icon Click", Toast.LENGTH_SHORT)
                    .show()
                onBackClick()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                    contentDescription = "Go Back",
                    tint = TrackerTheme.colors.primaryText

                )
            }
        },
        actions = {
            if (btnDeleteVisible) {
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = "Delete",
                        tint = TrackerTheme.colors.primaryText
                    )
                }
            }
        }
    )
    Divider(startIndent = 0.dp, thickness = 1.dp, color = TrackerTheme.colors.tintColor)
}
