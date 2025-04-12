package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.oltrysifp.arrowdrawer.composable.onArrow.CentralContent
import com.oltrysifp.arrowdrawer.composable.onArrow.MoveNode
import com.oltrysifp.arrowdrawer.composable.zoom.MutableZoomState
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineBoxProperties
import com.oltrysifp.arrowdrawer.models.enums.InheritType
import com.oltrysifp.arrowdrawer.models.enums.NodePosition
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
        (line.start.x - line.end.x) / 2,
        (line.start.y - line.end.y) / 2
    )

    val angle = atan2(line.end.y - line.start.y, line.end.x - line.start.x) * (180f / Math.PI).toFloat()
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
                    (line.end.x + midPoint.x).toInt(),
                    (line.end.y + midPoint.y).toInt()
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
                    line.start.x.toInt(),
                    line.start.y.toInt()
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
                    line.end.x.toInt(),
                    line.end.y.toInt()
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

@Composable
fun AttachControlsToLine(
    line: Line,
    zoomState: MutableZoomState,
    focusedLine: Line?,
    focusPoint: MutableState<Offset?>,
    actionStack: MutableList<Action>,
    inheritType: InheritType,

    pickerSetter: (Line?) -> Unit,
    focusedSetter: (Line?) -> Unit
) {
    val lineCopy = line.attachedCopy(zoomState.value.scale, -zoomState.value.offset)

    AttachToArrow(
        lineCopy,
        startContent = {
            MoveNode(
                focusedLine,
                line,
                focusPoint,
                zoomState.value.scale,
                actionStack,
                NodePosition.START
            )
        },
        endContent = {
            MoveNode(
                focusedLine,
                line,
                focusPoint,
                zoomState.value.scale,
                actionStack,
                NodePosition.END
            )
        },
        centerContent = { properties ->
            CentralContent(
                properties,
                line,
                zoomState.value.scale,
                onFocus = {
                    if (inheritType != InheritType.NONE) {
                        pickerSetter(line)
                    } else {
                        focusedSetter(line)
                    }
                }
            )
        }
    )
}