package com.oltrysifp.arrowdrawer.util

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object Palette {
    @Immutable
    data class CustomColorsPalette(
        val primary: Color = Color(0xFF8EAB8E),
        val secondary: Color = Color(0x528EAB8E),
        val red: Color = Color(0xFFF87265),
        val cancel: Color = Color(0xFF565656),
        val background: Color = Color.Unspecified,
        val surface: Color = Color.Unspecified,

        val onLine: Color = Color(0xFF181818),
        val onPrimary: Color = Color(0xFFFFFFFF),
        val onImage: Color = Color(0xFFF3F3F3),
        val onBackground: Color = Color.Unspecified,
        val onSurface: Color = Color.Unspecified,
        val onSurfaceText: Color = Color.Unspecified
    )

    val LightCustomColorsPalette = CustomColorsPalette(
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFE8E8E8),
        onBackground = Color(0xFF181818),
        onSurface = Color(0xFFCAD0CA),
        onSurfaceText = Color(0xFFCAD0CA)
    )

    val DarkCustomColorsPalette = CustomColorsPalette(
        background = Color(0xFFFFFBFE),
        surface = Color(0xFF131313),
        onBackground = Color(0xFFCEC4C4),
        onSurface = Color(0xFF2E332E),
        onSurfaceText = Color(0xFF2E332E)
    )

    @Composable
    fun buttonColors(
        container: Color = palette.primary
    ) = ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = palette.onPrimary
    )

    @Composable
    fun textField(
        container: Color = palette.onSurface
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
        container: Color = palette.surface
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

val LocalCustomColorsPalette = staticCompositionLocalOf { Palette.CustomColorsPalette() }
val palette: Palette.CustomColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColorsPalette.current