package com.oltrysifp.arrowdrawer.composable.arrowSettings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import com.oltrysifp.arrowdrawer.util.Constants
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun ThicknessPicker(
    thickness: MutableFloatState
) {
    Column {
        Slider(
            value = thickness.floatValue / 10f,
            onValueChange = {
                thickness.floatValue = it * Constants.MAX_THICKNESS
            },
            colors = SliderDefaults.colors(
                thumbColor = palette.primary,
                activeTrackColor = palette.primary,
                inactiveTrackColor = palette.onSurface
            )
        )
    }
}