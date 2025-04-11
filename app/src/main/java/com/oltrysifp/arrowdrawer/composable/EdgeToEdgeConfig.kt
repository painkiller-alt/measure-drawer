package com.oltrysifp.arrowdrawer.composable

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.oltrysifp.arrowdrawer.palette

@Composable
fun EdgeToEdgeConfig(
    activity: ComponentActivity,
    topColor: Color = Color(0x66FFFFFF),
    bottomColor: Color = Color(0x0DFFFFFF)
) {
    val topHash = topColor.hashCode()
    val bottomHash = bottomColor.hashCode()

    activity.enableEdgeToEdge(
        statusBarStyle = SystemBarStyle
            .light(topHash, Color.White.hashCode()),
        navigationBarStyle = SystemBarStyle
            .light(bottomHash, Color.White.hashCode())
    )
}