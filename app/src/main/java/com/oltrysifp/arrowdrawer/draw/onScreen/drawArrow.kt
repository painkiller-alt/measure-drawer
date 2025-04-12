package com.oltrysifp.arrowdrawer.draw.onScreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.oltrysifp.arrowdrawer.models.Line
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawArrow(
    line: Line
) {
    val start = line.start
    val end = line.end

    if (start != Offset(0f, 0f)) {
        drawLine(
            color = line.color,
            start = start,
            end = end,
            strokeWidth = line.thickness
        )
        val arrowHeadSize = (6f*line.thickness).coerceIn(10f, 100f)

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

        val xShift = 0.25f*line.thickness

        drawPath(
            path = Path().apply {
                moveTo(start.x+xShift, start.y)
                lineTo(startArrowPoint1.x, startArrowPoint1.y)
                moveTo(start.x+xShift, start.y)
                lineTo(startArrowPoint2.x, startArrowPoint2.y)
            },
            color = line.color,
            style = Stroke(width = line.thickness)
        )

        drawPath(
            path = Path().apply {
                moveTo(end.x-xShift, end.y)
                lineTo(endArrowPoint1.x, endArrowPoint1.y)
                moveTo(end.x-xShift, end.y)
                lineTo(endArrowPoint2.x, endArrowPoint2.y)
            },
            color = line.color,
            style = Stroke(width = line.thickness)
        )
    }
}