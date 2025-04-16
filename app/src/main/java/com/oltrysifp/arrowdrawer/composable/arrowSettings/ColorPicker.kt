package com.oltrysifp.arrowdrawer.composable.arrowSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oltrysifp.arrowdrawer.util.Constants
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun ColorPicker(
    color: MutableState<Color>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        Constants.lineColors.forEach {
            Box(
                contentAlignment = Alignment.Center
            ) {
                val border = if (color.value == it) {
                    Modifier.border(
                        BorderStroke(width = 2.dp, color = palette.onSurfaceText),
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }

                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .then(border)
                )

                Box(
                    Modifier
                        .clip(CircleShape)
                        .clickable { color.value = it }
                        .size(30.dp)
                        .background(it)
                        .border(
                            width = 1.dp,
                            color = palette.onBackground,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}