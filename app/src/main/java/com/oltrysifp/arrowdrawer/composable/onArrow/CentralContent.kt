package com.oltrysifp.arrowdrawer.composable.onArrow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.Palette
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineBoxProperties

@Composable
fun CentralContent(
    properties: LineBoxProperties,
    line: Line,
    screenImageScale: Float,
    onFocus: () -> Unit
) {
    val length = line.length()
    val showLength = line.mutatedLength()

    val cardSize = when {
        length*screenImageScale > 200 -> 1f
        else -> (length.toFloat() / 200)*screenImageScale
    }

    Row(
        Modifier
            .padding(6.dp)
            .offset {
                IntOffset(
                    x = - (properties.width / 2),
                    y = 0
                )
            }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Palette.theme.onLine
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .offset(
                    y = (-18).dp
                )
                .scale(
                    cardSize
                )
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    onFocus()
                },
            border = BorderStroke(
                width = 1.2.dp,
                color = line.color
            )
        ) {
            Text(
                "$showLength",
                fontSize = 14.sp,
                color = Palette.theme.onImage,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}