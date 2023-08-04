package com.mint.minttracker.recordFragment.composeViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.mint.minttracker.recordFragment.model.RecordParamUi
import com.mint.minttracker.theme.TrackerTheme

@Composable
fun RecordFieldItem(item: RecordParamUi) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (imageId, textId, valueId) = createRefs()
        Image(
            modifier = Modifier.constrainAs(imageId) {
                top.linkTo(textId.top)
                bottom.linkTo(textId.bottom)
                start.linkTo(parent.start, margin = 16.dp)
            },
            painter = painterResource(id = item.drawableResource),
            contentDescription = "image",
        )
        Text(
            modifier = Modifier
                .constrainAs(textId) {
                    top.linkTo(parent.top, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    start.linkTo(imageId.end, margin = 8.dp)
                },
            text = stringResource(id = item.textRes),
            style = TrackerTheme.typography.body,
            color = TrackerTheme.colors.primaryText
        )
        Text(
            modifier = Modifier.constrainAs(valueId) {
                top.linkTo(textId.top)
                bottom.linkTo(textId.bottom)
                end.linkTo(parent.end, margin = 16.dp)
            },
            text = item.value,
            style = TrackerTheme.typography.body,
            color = TrackerTheme.colors.primaryText
        )
    }
}
