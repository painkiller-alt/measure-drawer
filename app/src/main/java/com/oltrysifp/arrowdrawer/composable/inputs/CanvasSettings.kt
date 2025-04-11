package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oltrysifp.arrowdrawer.Palette
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.models.LineSettings
import com.oltrysifp.arrowdrawer.palette
import com.oltrysifp.arrowdrawer.util.log

@Composable
fun CanvasSettings(
    canvasSettings: MutableState<LineSettings>,

    onExit: (LineSettings) -> Unit,
    onInherit: () -> Unit
) {
    val color = remember { mutableStateOf(canvasSettings.value.color) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
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
                    Text("Настройки холста")

                    IconButton(
                        onClick = {
                            onExit(
                                LineSettings(
                                    color = color.value
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
                        )
                    }
                }
            }
        }
    }
}