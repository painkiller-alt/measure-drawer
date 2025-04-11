package com.oltrysifp.arrowdrawer.models

import androidx.compose.ui.graphics.Color

data class LineSettings (
    var thickness: Float = 5f,
    var color: Color = Color.Green,

    var customCoefficient: Float? = null,
    var customSize: Int? = null,
    var customUnit: String? = null
)