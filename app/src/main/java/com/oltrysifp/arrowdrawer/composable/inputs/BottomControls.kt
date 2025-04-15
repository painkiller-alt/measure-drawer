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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.R
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.enums.ExportState
import com.oltrysifp.arrowdrawer.util.palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomControls(
    actionStack: List<Action>,
    redoStack: List<Action>,
    focusedLine: Line?,
    drawMode: Boolean,

    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onEdit: () -> Unit,
    onExport: suspend () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit
) {
    var exportState by remember { mutableStateOf(ExportState.IDLE) }

    LaunchedEffect(exportState) {
        if (exportState == ExportState.DONE) {
            delay(2000)
            exportState = ExportState.IDLE
        }
    }

    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Row {
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

            AnimatedVisibility(
                redoStack.isNotEmpty()
            ) {
                IconButton(
                    onClick = { onRedo() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = palette.primary
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.undo),
                        "redo",
                        tint = palette.onPrimary,
                        modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                    )
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
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
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val exportCoroutine = rememberCoroutineScope()
            IconButton(
                onClick = {
                    exportCoroutine.launch {
                        exportState = ExportState.SAVING
                        onExport()
                        exportState = ExportState.DONE
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = palette.primary
                )
            ) {
                AnimatedContent(
                    exportState,
                    label = "exported"
                ) { exportState ->
                    if (exportState == ExportState.SAVING) {
                        CircularProgressIndicator(
                            color = palette.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            painterResource(
                                if (exportState == ExportState.DONE) R.drawable.download_done else R.drawable.download
                            ),
                            "download",
                            tint = palette.onPrimary
                        )
                    }
                }
            }
        }
    }
}