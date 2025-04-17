package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oltrysifp.arrowdrawer.R
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ColorPicker
import com.oltrysifp.arrowdrawer.composable.arrowSettings.FontSizePicker
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ThicknessPicker
import com.oltrysifp.arrowdrawer.composable.toast
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.util.Palette
import com.oltrysifp.arrowdrawer.util.log
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun EditMenu(
    line: Line,

    onExit: (Line) -> Unit,
    onDelete: () -> Unit,
    onInherit: () -> Unit
) {
    val length = remember { line.length() }
    val context = LocalContext.current

    val text = remember { mutableStateOf(line.mutatedLength().toInt().toString()) }

    val customUnit = remember { mutableStateOf(line.customUnit ?: "") }

    var isCustomCoefficient by remember { mutableStateOf(line.customCoefficient != null) }
    var isCustomSize by remember { mutableStateOf(line.customSize != null) }

    val color = remember { mutableStateOf(line.color) }
    val thickness = remember { mutableFloatStateOf(line.thickness) }
    val fontSize = remember { mutableFloatStateOf(line.fontSize) }

    fun getNewLine(): Line {
        val newLine = line.copy()

        val floatText = text.value.replace(",", ".") + "F"
        val floatValue = floatText.toFloat()

        val coefficient = floatValue / length
        if (isCustomCoefficient) {
            newLine.customCoefficient = coefficient
        } else if (isCustomSize) {
            newLine.customSize = floatValue
            log(newLine.customSize)
        } else {
            newLine.customCoefficient = null
            newLine.customSize = null
        }

        newLine.color = color.value
        newLine.thickness = thickness.floatValue
        newLine.fontSize = fontSize.floatValue

        val cu = customUnit.value
        if (cu != "") {
            newLine.customUnit = cu
        } else {
            newLine.customUnit = null
        }

        return newLine
    }

    LaunchedEffect(isCustomCoefficient, isCustomSize) {
        val newLine = getNewLine()
        text.value = newLine.mutatedLength().toInt().toString()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        try {
                            onExit(getNewLine())
                        } catch (e: Exception) {
                            context.toast("Ошибка")
                        }
                    }
                )
            }
            .background(Color(0x59000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = palette.surface
            ),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures()
                }
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .defaultMinSize(minHeight = 450.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row {
                        TextFieldDefault(
                            text,
                            enabled = isCustomSize,
                            modifier = Modifier.weight(1f),
                            maxSymbols = 10
                        )

                        AnimatedVisibility(
                            isCustomSize
                        ) {
                            Row(
                                modifier = Modifier
                                    .width(100.dp)
                            ) {
                                HSpacer(2.dp)

                                TextFieldDefault(
                                    customUnit,
                                    enabled = isCustomSize,
                                    maxSymbols = 4,
                                    placeholder = "ед.?"
                                )
                            }
                        }
                    }

                    VSpacer(2.dp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCustomSize,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    line.customSize = length
                                    isCustomSize = true
                                } else {
                                    line.customSize = null
                                    isCustomSize = false
                                    isCustomCoefficient = false
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = palette.primary
                            )
                        )

                        Text(
                            "Свой размер",
                            color = palette.onBackground
                        )
                    }

                    AnimatedVisibility(
                        isCustomSize
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isCustomCoefficient,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        line.customCoefficient = 1f
                                        isCustomCoefficient = true
                                    } else {
                                        line.customCoefficient = null
                                        isCustomCoefficient = false
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = palette.primary
                                )
                            )

                            Text(
                                "Динамический размер",
                                color = palette.onBackground
                            )
                        }
                    }

                    VSpacer(6.dp)

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

                VSpacer(2.dp)

                DefaultButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = Palette.buttonColors(
                        container = palette.red
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.trash),
                            "delete",
                            tint = palette.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )

                        HSpacer(2.dp)

                        Text(
                            "Удалить",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}