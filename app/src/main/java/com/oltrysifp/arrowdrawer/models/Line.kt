package com.oltrysifp.arrowdrawer.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

class Line(
    start: Offset,
    end: Offset,
    thickness: Float = 5f,
    color: Color = Color.Green,

    var customCoefficient: Float? = null,
    var customSize: Int? = null,
    var customUnit: String? = null
) {
    var start by mutableStateOf(start)
    var end by mutableStateOf(end)
    var thickness by mutableFloatStateOf(thickness)
    var color by mutableStateOf(color)

    fun copy(
        start: Offset = this.start,
        end: Offset = this.end,
        thickness: Float = this.thickness,
        color: Color = this.color,
        customCoefficient: Float? = this.customCoefficient,
        customSize: Int? = this.customSize,
        customUnit: String? = this.customUnit
    ): Line {
        return Line(
            start,
            end,
            thickness,
            color,
            customCoefficient,
            customSize,
            customUnit
        )
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

    fun length(): Int {
        val result = sqrt(
            (this.end.x - this.start.x).pow(2) +
               (this.end.y - this.start.y).pow(2)
        )

        return result.toInt()
    }

    fun mutatedLength(): Int {
        var coefficient: Float? = null
        this.customCoefficient?.let {
            coefficient = it
        }

        val customSize = this.customSize
        return if (customSize != null) {
            if (coefficient != null) {
                (this.length().toFloat() * coefficient!!).toInt()
            } else {
                (customSize.toFloat()).toInt()
            }
        } else {
            if (coefficient != null) {
                (this.length().toFloat() * coefficient!!).toInt()
            } else {
                this.length()
            }
        }
    }
}