package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oltrysifp.arrowdrawer.util.Palette

@Composable
fun TextFieldDefault(
    value: MutableState<String>,
    modifier: Modifier = Modifier,
    colors: TextFieldColors = Palette.textField(),
    maxSymbols: Int = 32,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    TextField(
        value.value,
        onValueChange = {
            if (it.length <= maxSymbols) {
                value.value = it
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = colors,
        enabled = enabled,
        modifier = modifier,
        singleLine = singleLine,
        placeholder = {
            if (placeholder != null) {
                Text(
                    placeholder,
                    color = Color.Gray
                )
            }
        }
    )
}