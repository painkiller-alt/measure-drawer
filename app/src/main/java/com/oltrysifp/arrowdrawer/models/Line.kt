package com.oltrysifp.arrowdrawer.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Line(
    start: Offset,
    end: Offset,
    thickness: Float = 5f,
    color: Color = Color.Green,
    fontSize: Float = 11f,

    customCoefficient: Float? = null,
    customSize: Int? = null,
    customUnit: String? = null,

    val hash: Int = Random.nextInt()
) {
    var start by mutableStateOf(start)
    var end by mutableStateOf(end)
    var thickness by mutableFloatStateOf(thickness)
    var color by mutableStateOf(color)
    var fontSize by mutableStateOf(fontSize)

    var customCoefficient by mutableStateOf(customCoefficient)
    var customSize by mutableStateOf(customSize)
    var customUnit by mutableStateOf(customUnit)

    fun copy(
        start: Offset = this.start,
        end: Offset = this.end,
        thickness: Float = this.thickness,
        color: Color = this.color,
        fontSize: Float = this.fontSize,
        customCoefficient: Float? = this.customCoefficient,
        customSize: Int? = this.customSize,
        customUnit: String? = this.customUnit,
        hash: Int = this.hash
    ): Line {
        return Line(
            start,
            end,
            thickness,
            color,
            fontSize,
            customCoefficient,
            customSize,
            customUnit,
            hash
        )
    }

    fun mutate(
        line: Line
    ) {
        this.start = line.start
        this.end = line.end
        this.color = line.color
        this.thickness = line.thickness
        this.fontSize = line.fontSize

        this.customUnit = line.customUnit
        this.customSize = line.customSize
        this.customCoefficient = line.customCoefficient
    }

    fun attachedCopy(
        imageScale: Float,
        imageOffset: Offset
    ): Line {
        return this.copy(
            start = this.start*imageScale + imageOffset,
            end = this.end*imageScale + imageOffset,
        )
    }

    fun length(): Float {
        val result = sqrt(
            (this.end.x - this.start.x).pow(2) +
               (this.end.y - this.start.y).pow(2)
        )

        return result
    }

    fun mutatedLength(): Float {
        var coefficient: Float? = null
        this.customCoefficient?.let {
            coefficient = it
        }

        val customSize = this.customSize
        return if (customSize != null) {
            if (coefficient != null) {
                this.length() * coefficient!!
            } else {
                customSize.toFloat()
            }
        } else {
            if (coefficient != null) {
                this.length() * coefficient!!
            } else {
                this.length()
            }
        }
    }


    // Convert to serializable DTO
    fun toDto(): LineDto = LineDto(
        startX = start.x,
        startY = start.y,
        endX = end.x,
        endY = end.y,
        thickness = thickness,
        color = color.toArgb(),
        fontSize = fontSize,
        customCoefficient = customCoefficient,
        customSize = customSize,
        customUnit = customUnit,
        hash = hash
    )

    companion object {
        // Recreate from DTO
        fun fromDto(dto: LineDto): Line = Line(
            start = Offset(dto.startX, dto.startY),
            end = Offset(dto.endX, dto.endY),
            thickness = dto.thickness,
            color = Color(dto.color),
            fontSize = dto.fontSize,
            customCoefficient = dto.customCoefficient,
            customSize = dto.customSize,
            customUnit = dto.customUnit,
            hash = dto.hash
        )
    }
}


@Serializable
data class LineDto(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val thickness: Float,
    val color: Int,
    val fontSize: Float,
    val customCoefficient: Float?,
    val customSize: Int?,
    val customUnit: String?,
    val hash: Int
)