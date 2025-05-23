package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ColorPicker
import com.oltrysifp.arrowdrawer.composable.arrowSettings.FontSizePicker
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ThicknessPicker
import com.oltrysifp.arrowdrawer.models.LineSettings
import com.oltrysifp.arrowdrawer.util.Palette
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun CanvasSettings(
    canvasSettings: LineSettings,

    onExit: (LineSettings) -> Unit,
    onInherit: () -> Unit
) {
    val color = remember { mutableStateOf(canvasSettings.color) }
    val thickness = remember { mutableFloatStateOf(canvasSettings.thickness) }
    val fontSize = remember { mutableFloatStateOf(canvasSettings.fontSize) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { /* Consumes all taps */ }
            }
            .background(palette.background)
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Настройки холста",
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = {
                            onExit(
                                LineSettings(
                                    color = color.value,
                                    thickness = thickness.floatValue,
                                    fontSize = fontSize.floatValue
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            "close"
                        )
                    }
                }

                VSpacer(20.dp)

                ColorPicker(color)

                VSpacer(6.dp)

                ThicknessPicker(thickness)

                VSpacer(6.dp)

                FontSizePicker(fontSize)

                VSpacer(4.dp)

                DefaultButton(
                    onClick = {
                        onInherit()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = Palette.buttonColors(
                        container = palette.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Наследовать параметры",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}