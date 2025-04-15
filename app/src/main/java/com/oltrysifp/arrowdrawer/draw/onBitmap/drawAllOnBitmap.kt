package com.oltrysifp.arrowdrawer.draw.onBitmap

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.composable.AttachToArrow
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.util.bitmap.saveImage
import com.oltrysifp.arrowdrawer.util.palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun drawAllOnBitmap(
    bitmap: Bitmap,
    lineList: List<Line>
): Bitmap {
    var bt = bitmap
    for (line in lineList) {
        val lineCopy = line.copy(
            start = line.start,
            end = line.end,
        )

        val result = drawArrowOnBitmap(
            lineCopy,
            bt
        )

        if (result != null) {
            bt = result
        }
    }

    return bt
}

suspend fun drawAllAndExport(
    bitmap: Bitmap,
    lineList: List<Line>,

    context: Context,
    activity: ComponentActivity,
    folder: String = "MeasurerResult"
) {
    val controlsBitmap = activity.composableToBitmap(bitmap.width, bitmap.height) {
        for (line in lineList) {
            AttachToArrow(
                line,
                startContent = {},
                endContent = {},
                centerContent = {
                    val length = line.length()
                    val mutatedLength = line.mutatedLength()
                    val lengthText = if (length < 10) "%.2f".format(mutatedLength) else mutatedLength.toInt().toString()
                    val unitText = if (line.customUnit != null) line.customUnit else ""

                    val cardSize = when {
                        length > 200 -> 1f
                        else -> (length / 200)
                    }

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
            )
        }
    }

    val bt = drawAllOnBitmap(
        bitmap,
        lineList
    )

    val combinedBitmap = combineBitmaps(bt, controlsBitmap)

    withContext(Dispatchers.IO) {
        saveImage(
            combinedBitmap,
            context,
            folder
        )
    }
}