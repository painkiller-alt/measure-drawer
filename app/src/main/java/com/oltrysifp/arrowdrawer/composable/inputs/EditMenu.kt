package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oltrysifp.arrowdrawer.R
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ColorPicker
import com.oltrysifp.arrowdrawer.composable.arrowSettings.ThicknessPicker
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
    val length = line.length()

    val text = remember { mutableStateOf(line.mutatedLength().toInt().toString()) }

    val customUnit = remember { mutableStateOf(line.customUnit ?: "") }

    var isCustomCoefficient by remember { mutableStateOf(line.customCoefficient != null) }
    var isCustomSize by remember { mutableStateOf(line.customSize != null) }

    val color = remember { mutableStateOf(line.color) }
    val thickness = remember { mutableFloatStateOf(line.thickness) }

    fun getNewLine(): Line {
        val newLine = line.copy()

        val coefficient = text.value.toInt().toFloat() / length
        if (isCustomCoefficient) {
            newLine.customCoefficient = coefficient
        } else if (isCustomSize) {
            newLine.customSize = text.value.toInt()
        } else {
            newLine.customCoefficient = null
            newLine.customSize = null
        }

        newLine.color = color.value
        newLine.thickness = thickness.floatValue

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

    Dialog(
        onDismissRequest = {
            try {
                onExit(getNewLine())
            } catch (e: Error) {

            }
        }
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = palette.surface
            ),
            modifier = Modifier.height(450.dp)
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxHeight(),
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
                                    .width(80.dp)
                            ) {
                                HSpacer(2.dp)

                                TextFieldDefault(
                                    customUnit,
                                    enabled = isCustomSize,
                                    maxSymbols = 3,
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
                                    line.customSize = length.toInt()
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
                            color = palette.onSurfaceText
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
                                color = palette.onSurfaceText
                            )
                        }
                    }

                    VSpacer(6.dp)

                    ColorPicker(color)

                    VSpacer(4.dp)

                    ThicknessPicker(thickness)

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
                        )
                    }
                }
            }
        }
    }
}