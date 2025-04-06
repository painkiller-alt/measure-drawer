package com.oltrysifp.arrowdrawer.models

import android.util.Half.toFloat
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

class Line(
    var start: MutableState<Offset>,
    var end: MutableState<Offset>,
    var thickness: Float = 5f,
    var color: Color = Color.Green,

    var customCoefficient: Float? = null,
    var customSize: Int? = null,
    var customUnit: String? = null
) {
    fun copy(
        start: MutableState<Offset> = this.start,
        end: MutableState<Offset> = this.end,
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
            start = mutableStateOf(this.start.value*imageScale + imageOffset),
            end = mutableStateOf(this.end.value*imageScale + imageOffset),
        )
    }

    fun length(): Int {
        val result = sqrt(
            (this.end.value.x - this.start.value.x).pow(2) +
               (this.end.value.y - this.start.value.y).pow(2)
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