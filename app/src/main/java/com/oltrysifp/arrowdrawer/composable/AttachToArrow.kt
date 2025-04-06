package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineBoxProperties
import kotlin.math.atan2

@Composable
fun AttachToArrow(
    line: Line,
    startContent: @Composable () -> Unit = {},
    centerContent: @Composable (LineBoxProperties) -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    var elementWidth by remember { mutableIntStateOf(0) }

    val midPoint = Offset(
        (line.start.value.x - line.end.value.x) / 2,
        (line.start.value.y - line.end.value.y) / 2
    )

    val angle = atan2(line.end.value.y - line.start.value.y, line.end.value.x - line.start.value.x) * (180f / Math.PI).toFloat()
    val props = LineBoxProperties(
        width = elementWidth,
        angle = angle
    )

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                elementWidth = coordinates.size.width // Get width and height in pixels
            }
            .offset {
                IntOffset(
                    (line.end.value.x + midPoint.x).toInt(),
                    (line.end.value.y + midPoint.y).toInt()
                )
            }
            .graphicsLayer {
                this.transformOrigin = TransformOrigin(
                    pivotFractionX = 0f,
                    pivotFractionY = 0f
                )
                if (angle < 90 && angle > -90) {
                    this.rotationZ = angle
                } else {
                    this.rotationZ = angle + 180
                }
            },
        contentAlignment = Alignment.Center
    ) {
        centerContent(props)
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    line.start.value.x.toInt(),
                    line.start.value.y.toInt()
                )
            }
            .graphicsLayer {
                this.transformOrigin = TransformOrigin(
                    pivotFractionX = 0f,
                    pivotFractionY = 0f
                )
            }
    ) {
        startContent()
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    line.end.value.x.toInt(),
                    line.end.value.y.toInt()
                )
            }
            .graphicsLayer {
                this.transformOrigin = TransformOrigin(
                    pivotFractionX = 0f,
                    pivotFractionY = 0f
                )
            }
    ) {
        endContent()
    }
}