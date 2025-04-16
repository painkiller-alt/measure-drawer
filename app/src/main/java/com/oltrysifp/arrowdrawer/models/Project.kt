package com.oltrysifp.arrowdrawer.models

import android.graphics.Bitmap
import kotlinx.serialization.Serializable

data class Project (
    val name: String,
    val image: Bitmap,
    val objects: List<Line>,
    val lastEdit: Long = System.currentTimeMillis(),
    val settings: LineSettings = LineSettings()
)

@Serializable
data class ProjectMeta(
    val lastEdit: Long,
    val settings: LineSettings
)