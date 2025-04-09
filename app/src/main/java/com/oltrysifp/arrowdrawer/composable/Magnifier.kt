package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import nl.birdly.zoombox.ZoomState

@Composable
fun ArrowMagnifier(
    focusPoint: MutableState<MutableState<Offset>?>,
    zoomState: ZoomState
) {
    focusPoint.value?.let { focus ->
        Box(
            modifier = Modifier
                .magnifier(
                    sourceCenter = { focus.value * zoomState.scale - zoomState.offset },
                    magnifierCenter = { Offset.Zero },
                    zoom = 2f,
                    size = DpSize(
                        width = 120.dp,
                        height = 120.dp
                    )
                )
        )
    }
}