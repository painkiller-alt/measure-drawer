package com.oltrysifp.arrowdrawer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object Palette {
    abstract class Theme {
        abstract val background: Color
        abstract val primary: Color
        abstract val secondary: Color
        abstract val surface: Color
        abstract val red: Color

        abstract val onLine: Color
        abstract val onBackground: Color
        abstract val onPrimary: Color
        abstract val onImage: Color
        abstract val onSurface: Color
        abstract val onSurfaceText: Color
    }

    object Light: Theme() {
        override val background = Color(0xFFFFFBFE)
        override val primary = Color(0xFF8EAB8E)
        override val secondary = Color(0x528EAB8E)
        override val surface = Color(0xFFE8E8E8)
        override val red = Color(0xFFF87265)

        override val onLine = Color(0xFF181818)
        override val onBackground = Color(0xFF181818)
        override val onPrimary = Color(0xFFFFFFFF)
        override val onImage = Color(0xFFF3F3F3)
        override val onSurface = Color(0xFFCAD0CA)
        override val onSurfaceText = Color(0xFF050505)
    }

    object Dark: Theme() {
        override val background = Color(0xFF0E1109)
        override val primary = Color(0xFF8EAB8E)
        override val secondary = Color(0x528EAB8E)
        override val surface = Color(0xFF131313)
        override val red = Color(0xFFF87265)

        override val onLine = Color(0xFF181818)
        override val onBackground = Color(0xFFCEC4C4)
        override val onPrimary = Color(0xFFFFFFFF)
        override val onImage = Color(0xFFF3F3F3)
        override val onSurface = Color(0xFF2E332E)
        override val onSurfaceText = Color(0xFFF6F6F6)
    }

    val theme: Theme
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Dark else Light

    @Composable
    fun buttonColors(
        container: Color = theme.primary
    ) = ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = theme.onPrimary
    )

    @Composable
    fun textField(
        container: Color = theme.onSurface
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
        container: Color = theme.surface
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