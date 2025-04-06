package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun ArrowMagnifier(
    focusPoint: MutableState<MutableState<Offset>?>,
    screenImageScale: Float,
    imageOffset: Offset
) {
    focusPoint.value?.let { focus ->
        Box(
            modifier = Modifier
                .magnifier(
                    sourceCenter = {
                        Offset(
                            focus.value.x,
                            focus.value.y
                        ) * screenImageScale + imageOffset
                    },
                    magnifierCenter = {
                        Offset(
                            x=0f,
                            y=0f
                        )
                    },
                    zoom = 2f,
                    size = DpSize(
                        width = 120.dp,
                        height = 120.dp
                    )
                )
        )
    }
}