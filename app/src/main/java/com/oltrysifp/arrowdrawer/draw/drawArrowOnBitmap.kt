package com.oltrysifp.arrowdrawer.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import com.oltrysifp.arrowdrawer.models.Line
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun drawArrowOnBitmap(
    line: Line,
    bitmap: Bitmap
): Bitmap? {
    val start = line.start.value
    val end = line.end.value

    val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    if (start != Offset(0f, 0f)) {
        val canvas = Canvas(newBitmap)
        val paint = Paint().apply {
            color = line.color.hashCode()
            strokeWidth = line.thickness
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        canvas.drawLine(start.x, start.y, end.x, end.y, paint)

        val arrowHeadSize = 30f

        val arrowAngle = Math.toRadians(30.0) // 30-degree arrowhead angle
        val angle = atan2(end.y - start.y, end.x - start.x)

        val endArrowPoint1 = Offset(
            end.x - arrowHeadSize * cos(angle - arrowAngle).toFloat(),
            end.y - arrowHeadSize * sin(angle - arrowAngle).toFloat()
        )
        val endArrowPoint2 = Offset(
            end.x - arrowHeadSize * cos(angle + arrowAngle).toFloat(),
            end.y - arrowHeadSize * sin(angle + arrowAngle).toFloat()
        )

        val startArrowPoint1 = Offset(
            start.x + arrowHeadSize * cos(angle - arrowAngle).toFloat(),
            start.y + arrowHeadSize * sin(angle - arrowAngle).toFloat()
        )
        val startArrowPoint2 = Offset(
            start.x + arrowHeadSize * cos(angle + arrowAngle).toFloat(),
            start.y + arrowHeadSize * sin(angle + arrowAngle).toFloat()
        )

        val pathEnd = Path().apply {
            moveTo(end.x, end.y)
            lineTo(endArrowPoint1.x, endArrowPoint1.y)
            moveTo(end.x, end.y)
            lineTo(endArrowPoint2.x, endArrowPoint2.y)
        }
        val pathStart = Path().apply {
            moveTo(start.x, start.y)
            lineTo(startArrowPoint1.x, startArrowPoint1.y)
            moveTo(start.x, start.y)
            lineTo(startArrowPoint2.x, startArrowPoint2.y)
        }

        canvas.drawPath(pathEnd, paint)
        canvas.drawPath(pathStart, paint)
    }

    return newBitmap
}