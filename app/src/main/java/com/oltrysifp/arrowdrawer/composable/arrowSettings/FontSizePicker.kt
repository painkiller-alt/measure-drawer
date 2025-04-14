package com.oltrysifp.arrowdrawer.composable.arrowSettings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import com.oltrysifp.arrowdrawer.util.Constants
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun FontSizePicker(
    fontSize: MutableFloatState
) {
    Column {
        Text(
            "Размер шрифта"
        )

        Slider(
            value = (fontSize.floatValue-Constants.MIN_FONT) / Constants.MAX_FONT,
            onValueChange = {
                fontSize.floatValue = (it * Constants.MAX_FONT)+Constants.MIN_FONT
            },
            colors = SliderDefaults.colors(
                thumbColor = palette.primary,
                activeTrackColor = palette.primary,
                inactiveTrackColor = palette.onSurface
            )
        )
    }
}