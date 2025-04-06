package com.oltrysifp.arrowdrawer.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HSpacer(
    space: Dp
) {
    Spacer(
        Modifier.padding(
            horizontal = space
        )
    )
}

@Composable
fun VSpacer(
    space: Dp
) {
    Spacer(
        Modifier.padding(
            vertical = space
        )
    )
}