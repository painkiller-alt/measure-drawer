package com.oltrysifp.arrowdrawer.composable.onArrow

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oltrysifp.arrowdrawer.Palette
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.ui.theme.Secondary
import com.oltrysifp.arrowdrawer.util.log
import nl.birdly.zoombox.ZoomState

@Composable
fun StartContent(
    focusedLine: Line?,
    line: Line,
    focusPoint: MutableState<MutableState<Offset>?>,
    scale: Float
) {
    // what ?? Try to fix it. Scale somehow is 1.0 in onDrag { } so i worked THIS out
    var sc by remember { mutableFloatStateOf(scale) }
    sc = scale

    if (focusedLine == line) {
        Surface(
            modifier = Modifier
                .size(30.dp)
                .offset(
                    y = (-15).dp,
                    x = (-15).dp
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            focusPoint.value = line.start
                        },
                        onDrag = { _, dragAmount ->
                            line.start.value += dragAmount / sc
                        },
                        onDragEnd = {
                            focusPoint.value = null
                        }
                    )
                },
            color = MaterialTheme.colorScheme.Secondary,
            shape = CircleShape
        ) {

        }
    }
}