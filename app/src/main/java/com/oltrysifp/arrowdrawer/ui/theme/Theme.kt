package com.oltrysifp.arrowdrawer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.oltrysifp.arrowdrawer.util.LocalCustomColorsPalette
import com.oltrysifp.arrowdrawer.util.Palette

private val DarkColorScheme = darkColorScheme(
    background = Palette.DarkCustomColorsPalette.background
)

private val LightColorScheme = lightColorScheme(
    background = Palette.LightCustomColorsPalette.background
)

@Composable
fun ArrowDrawerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val palette =
        if (darkTheme) Palette.DarkCustomColorsPalette
        else Palette.LightCustomColorsPalette

    CompositionLocalProvider(
        LocalCustomColorsPalette provides palette // our custom palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}