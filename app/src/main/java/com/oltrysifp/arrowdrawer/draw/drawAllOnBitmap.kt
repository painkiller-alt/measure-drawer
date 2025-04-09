package com.oltrysifp.arrowdrawer.draw

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import com.oltrysifp.arrowdrawer.bitmap.saveImage
import com.oltrysifp.arrowdrawer.models.Line

fun drawAllOnBitmap(
    bitmap: Bitmap,
    lineList: List<Line>,
    imageScale: Float
): Bitmap {
    var bt = bitmap
    for (line in lineList) {
        val lineCopy = line.copy(
            start = mutableStateOf(line.start.value/imageScale),
            end = mutableStateOf(line.end.value/imageScale),
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

fun drawAllAndExport(
    bitmap: Bitmap?,
    lineList: List<Line>,
    imageScale: Float,

    context: Context,
    folder: String = "ArrowDrawerResult"
) {
    bitmap?.let {
        val bt = drawAllOnBitmap(
            bitmap,
            lineList,
            imageScale
        )

        saveImage(
            bt,
            context,
            folder
        )
    }
}