package com.oltrysifp.arrowdrawer.draw.onScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oltrysifp.arrowdrawer.composable.zoom.MutableZoomState
import com.oltrysifp.arrowdrawer.models.Line

@Composable
fun DrawAllOnScreen(
    lineList: List<Line>,
    zoomState: MutableZoomState
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        for (line in lineList) {
            val lineCopy = line.attachedCopy(zoomState.value.scale, -zoomState.value.offset)
            drawArrow(lineCopy)
        }
    }
}