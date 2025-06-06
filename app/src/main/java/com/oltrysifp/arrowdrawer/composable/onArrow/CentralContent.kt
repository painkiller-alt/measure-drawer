package com.oltrysifp.arrowdrawer.composable.onArrow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun CentralContent(
    line: Line,
    screenImageScale: Float,
    onFocus: () -> Unit
) {
    val length = line.length()
    val mutatedLength = line.mutatedLength()
    val lengthText = if (mutatedLength < 10)
        "%.2f".format(mutatedLength) else mutatedLength.toInt().toString()
    val unitText = if (line.customUnit != null) line.customUnit else ""

    val cardSize = when {
        length*screenImageScale > 200 -> 1f
        else -> (length / 200)*screenImageScale
    }

    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onFocus() }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = palette.onLine
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .scale(cardSize)
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp)),
            border = BorderStroke(
                width = (line.thickness*0.24f).dp,
                color = line.color
            )
        ) {
            Text(
                lengthText + unitText,
                fontSize = line.fontSize.sp,
                lineHeight = 16.sp,
                color = palette.onImage,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}