package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.composable.VSpacer
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineSettings
import com.oltrysifp.arrowdrawer.util.Palette
import com.oltrysifp.arrowdrawer.util.palette

@Composable
fun SureToInherit(
    lineFrom: Line,

    onInherit: (LineSettings) -> Unit,
    onCancel: () -> Unit
) {
    val colorInh = remember { mutableStateOf(true) }
    val thicknessInh = remember { mutableStateOf(true) }
    val customUnitInh = remember { mutableStateOf(true) }
    val customCoefficientInh = remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = { onCancel() }
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
                    Text("Параметры, которые перейдут от выбранной фигуры")

                    VSpacer(8.dp)

                    Option(colorInh, "Цвет")
                    Option(thicknessInh, "Толщина")
                    lineFrom.customUnit?.let { Option(customUnitInh, "Ед. Измерения") }
                    lineFrom.customCoefficient?.let { Option(customCoefficientInh, "Коофициент размера") }
                }

                Row {
                    DefaultButton(
                        onClick = {
                            onCancel()
                        },
                        colors = Palette.buttonColors(palette.cancel),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Отмена",
                            fontSize = 14.sp
                        )
                    }

                    HSpacer(6.dp)

                    DefaultButton(
                        onClick = {
                            val newSettings = LineSettings()
                            if (thicknessInh.value) newSettings.thickness = lineFrom.thickness
                            if (colorInh.value) newSettings.color = lineFrom.color
                            if (customUnitInh.value) newSettings.customUnit = lineFrom.customUnit
                            if (customCoefficientInh.value) newSettings.customCoefficient = lineFrom.customCoefficient
                            onInherit(newSettings)
                        },
                        colors = Palette.buttonColors(palette.primary)
                    ) {
                        Text(
                            "Наследовать",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Option(
    checked: MutableState<Boolean>,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked.value,
            onCheckedChange = {
                checked.value = it
            },
            colors = CheckboxDefaults.colors(
                checkedColor = palette.primary
            )
        )

        Text(text)
    }
}