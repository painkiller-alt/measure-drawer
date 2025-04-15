package com.oltrysifp.arrowdrawer.draw.onBitmap

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap

fun Activity.composableToBitmap(
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): Bitmap {
    val composeView = ComposeView(this).apply {
        setContent {
            Box(modifier = Modifier.size(width.dp, height.dp)) {
                content()
            }
        }
    }

    // Add to decor view temporarily
    val rootView = window.decorView as ViewGroup
    rootView.addView(composeView)

    return try {
        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        composeView.layout(0, 0, width, height)
        composeView.drawToBitmap()
    } finally {
        rootView.removeView(composeView)
    }
}

fun combineBitmaps(background: Bitmap, foreground: Bitmap): Bitmap {
    val combined = Bitmap.createBitmap(
        background.width,
        background.height,
        background.config ?: Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(combined)
    canvas.drawBitmap(background, 0f, 0f, null)
    canvas.drawBitmap(foreground, 0f, 0f, null)
    return combined
}
