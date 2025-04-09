package com.oltrysifp.arrowdrawer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(

)

private val LightColorScheme = lightColorScheme(

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val ColorScheme.Background: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFFBFE) else Color(0xFFFFFBFE)
val ColorScheme.Primary: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFF8EAB8E) else Color(0xFF8EAB8E)
val ColorScheme.Secondary: Color @Composable get() = if (isSystemInDarkTheme()) Color(0x528EAB8E) else Color(0x528EAB8E)
val ColorScheme.Surface: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFF131313) else Color(0xFFE8E8E8)
val ColorScheme.Red: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF87265) else Color(0xFFF87265)
val ColorScheme.OnLine: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFF181818) else Color(0xFF181818)
val ColorScheme.OnBackground: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFFCEC4C4) else Color(0xFF181818)
val ColorScheme.OnPrimary: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFFFFFFFF)
val ColorScheme.OnImage: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF3F3F3) else Color(0xFFF3F3F3)
val ColorScheme.OnSurface: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E332E) else Color(0xFFCAD0CA)
val ColorScheme.OnSurfaceText: Color @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E332E) else Color(0xFFCAD0CA)