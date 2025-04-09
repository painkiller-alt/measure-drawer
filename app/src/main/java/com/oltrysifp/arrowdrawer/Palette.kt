package com.oltrysifp.arrowdrawer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.oltrysifp.arrowdrawer.ui.theme.OnPrimary
import com.oltrysifp.arrowdrawer.ui.theme.OnSurface
import com.oltrysifp.arrowdrawer.ui.theme.Primary
import com.oltrysifp.arrowdrawer.ui.theme.Surface

object Palette {
    @Composable
    fun buttonColors(
        container: Color = MaterialTheme.colorScheme.Primary
    ) = ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = MaterialTheme.colorScheme.OnPrimary
    )

    @Composable
    fun textField(
        container: Color = MaterialTheme.colorScheme.OnSurface
    ) = TextFieldDefaults.colors(
        focusedContainerColor = container,
        disabledContainerColor = container,
        unfocusedContainerColor = container,

        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )

    @Composable
    fun textFieldDisabled(
        container: Color = MaterialTheme.colorScheme.Surface
    ) = TextFieldDefaults.colors(
        focusedContainerColor = container,
        disabledContainerColor = container,
        unfocusedContainerColor = container,

        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
}