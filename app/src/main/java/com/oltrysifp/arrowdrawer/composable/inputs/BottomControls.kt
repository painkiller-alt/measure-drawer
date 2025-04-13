package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.R
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.util.palette
import kotlinx.coroutines.delay

@Composable
fun BottomControls(
    actionStack: List<Action>,
    focusedLine: Line?,
    drawMode: Boolean,

    onUndo: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit
) {
    var isExported by remember { mutableStateOf(false) }

    LaunchedEffect(isExported) {
        delay(2000)
        isExported = false
    }

    Row(
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                AnimatedVisibility(
                    actionStack.isNotEmpty()
                ) {
                    IconButton(
                        onClick = { onUndo() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = palette.primary
                        )
                    ) {
                        Icon(
                            painterResource(R.drawable.undo),
                            "undo",
                            tint = palette.onPrimary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onSettings()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = palette.primary
                    )
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        "settings",
                        tint = palette.onPrimary
                    )
                }
            }

            IconButton(
                onClick = { onAdd() },
                colors = if (!drawMode) IconButtonDefaults.iconButtonColors(
                    containerColor = palette.primary
                ) else IconButtonDefaults.iconButtonColors(
                    containerColor = palette.background,
                )
            ) {
                Icon(
                    Icons.Filled.Add,
                    "add",
                    tint = if (!drawMode) palette.onPrimary else palette.onBackground,
                )
            }

            HSpacer(2.dp)

            if (focusedLine != null) {
                val length = focusedLine.mutatedLength()
                val lengthText = if (length < 10) "%.2f".format(length) else length.toInt().toString()

                DefaultButton(
                    onClick = {
                        onEdit()
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.arrow),
                        "arrow",
                        modifier = Modifier.size(19.dp)
                    )

                    HSpacer(2.dp)

                    Text(
                        lengthText,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Row {
            IconButton(
                onClick = {
                    onExport()
                    isExported = true
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = palette.primary
                )
            ) {
                AnimatedContent(
                    isExported,
                    label = "exported"
                ) { isExported ->
                    Icon(
                        painterResource(if (isExported) R.drawable.download_done else R.drawable.download),
                        "download",
                        tint = palette.onPrimary
                    )
                }
            }
        }
    }
}