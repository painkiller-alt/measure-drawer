package com.oltrysifp.arrowdrawer.draw.onBitmap

import android.content.Context
import android.graphics.Bitmap
import com.oltrysifp.arrowdrawer.bitmap.saveImage
import com.oltrysifp.arrowdrawer.models.Line

fun drawAllOnBitmap(
    bitmap: Bitmap,
    lineList: List<Line>,
    scaleC: Float
): Bitmap {
    var bt = bitmap
    for (line in lineList) {
        val lineCopy = line.copy(
            start = line.start/scaleC,
            end = line.end/scaleC,
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
    bitmap: Bitmap,
    lineList: List<Line>,

    context: Context,
    folder: String = "ArrowDrawerResult"
) {
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val scaleC: Float = screenWidth.toFloat() / bitmap.width

    val bt = drawAllOnBitmap(
        bitmap,
        lineList,
        scaleC
    )

    saveImage(
        bt,
        context,
        folder
    )
}