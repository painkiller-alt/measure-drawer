package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.palette

@Composable
fun SureToInherit(
    lineFrom: Line,
    lineTo: Line,

    onCancel: () -> Unit
) {
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
                Text("Выберите параметры, которые наследует линия")
            }
        }
    }
}