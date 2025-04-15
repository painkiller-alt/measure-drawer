package com.oltrysifp.arrowdrawer.composable.onArrow

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.ChangeAction
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.enums.NodePosition
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun MoveNode(
    line: Line,
    focusPoint: MutableState<Offset?>,
    scale: Float,
    actionStack: MutableList<Action>,
    pos: NodePosition
) {
    var startPos by remember { mutableStateOf(line) }

    Surface(
        modifier = Modifier
            .size(30.dp)
            .pointerInput(line, scale) {
                detectDragGestures(
                    onDragStart = {
                        startPos = line.copy()
                    },
                    onDrag = { _, dragAmount ->
                        if (pos == NodePosition.START) {
                            line.start += dragAmount / scale
                            focusPoint.value = line.start
                        } else {
                            line.end += dragAmount / scale
                            focusPoint.value = line.end
                        }
                    },
                    onDragEnd = {
                        focusPoint.value = null
                        actionStack.add(
                            ChangeAction(
                                startPos,
                                line
                            )
                        )
                    }
                )
            },
        color = palette.secondary,
        shape = CircleShape
    ) {

    }
}