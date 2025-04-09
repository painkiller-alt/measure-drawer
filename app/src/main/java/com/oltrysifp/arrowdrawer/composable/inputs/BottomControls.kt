package com.oltrysifp.arrowdrawer.composable.inputs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oltrysifp.arrowdrawer.Palette
import com.oltrysifp.arrowdrawer.R
import com.oltrysifp.arrowdrawer.composable.HSpacer
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.ui.theme.OnBackground

@Composable
fun BottomControls(
    isLoaded: Boolean,

    focusedLine: Line?,

    onEdit: () -> Unit,
    onLoad: () -> Unit,
    onExport: () -> Unit,
) {
    var isExported by remember { mutableStateOf(false) }

    AnimatedContent(
        isLoaded,
        label = "button"
    ) {
        if (!it) {
            DefaultButton(
                onClick = onLoad
            ) {
                Text("Выбрать изображение")
            }
        } else {
            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (focusedLine != null) {
                        val showLength = focusedLine.mutatedLength()

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
                                "$showLength",
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = {
                            onExport()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            painterResource(R.drawable.download),
                            "download",
                            tint = MaterialTheme.colorScheme.OnBackground
                        )
                    }
                }
            }
        }
    }
}