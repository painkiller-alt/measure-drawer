package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import com.oltrysifp.arrowdrawer.composable.onArrow.CentralContent
import com.oltrysifp.arrowdrawer.composable.onArrow.MoveNode
import com.oltrysifp.arrowdrawer.composable.zoom.MutableZoomState
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.enums.InheritType
import com.oltrysifp.arrowdrawer.models.enums.NodePosition
import kotlin.math.atan2

@Composable
fun AttachToArrow(
    line: Line,
    startContent: @Composable () -> Unit = {},
    centerContent: @Composable () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    val midPoint = Offset(
        (line.start.x + line.end.x) / 2,
        (line.start.y + line.end.y) / 2
    )

    val angle = atan2(line.end.y - line.start.y, line.end.x - line.start.x) * (180f / Math.PI).toFloat()

    // Custom layout that centers content at the midpoint
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        if (angle < 90 && angle > -90) {
                            this.rotationZ = angle
                        } else {
                            this.rotationZ = angle + 180
                        }
                        transformOrigin = TransformOrigin.Center
                    },
                contentAlignment = Alignment.Center
            ) {
                centerContent()
            }

            Box {startContent()}
            Box {endContent()}
        },
        modifier = Modifier
    ) { measurables, constraints ->
        val (centerMeasurable, startMeasurable, endMeasurable) = measurables

        val centerPlaceable = centerMeasurable.measure(constraints)
        val startPlaceable = startMeasurable.measure(constraints)
        val endPlaceable = endMeasurable.measure(constraints)

        layout(centerPlaceable.width, centerPlaceable.height) {
            centerPlaceable.placeRelative(
                x = (midPoint.x - centerPlaceable.width / 2).toInt(),
                y = (midPoint.y - centerPlaceable.height / 2).toInt()
            )

            startPlaceable.placeRelative(
                x = (line.start.x - startPlaceable.width / 2).toInt(),
                y = (line.start.y - startPlaceable.height / 2).toInt()
            )

            endPlaceable.placeRelative(
                x = (line.end.x - endPlaceable.width / 2).toInt(),
                y = (line.end.y - endPlaceable.height / 2).toInt()
            )
        }
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
            if (focusedLine == line) {
                MoveNode(
                    line,
                    focusPoint,
                    zoomState.value.scale,
                    actionStack,
                    NodePosition.START
                )
            }
        },
        endContent = {
            if (focusedLine == line) {
                MoveNode(
                    line,
                    focusPoint,
                    zoomState.value.scale,
                    actionStack,
                    NodePosition.END
                )
            }
        },
        centerContent = {
            CentralContent(
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