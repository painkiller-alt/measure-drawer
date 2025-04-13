package com.oltrysifp.arrowdrawer.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.oltrysifp.arrowdrawer.models.Project
import java.io.File

fun loadAllProjectsFromAppStorage(context: Context): List<Project> {
    val projects = mutableListOf<Project>()

    val appDir = context.getExternalFilesDir(null) ?: return projects
    appDir.listFiles()?.forEach { directory ->
        if (directory.isDirectory) {
            val originFile = File(directory, "origin.png")

            if (originFile.exists() && originFile.isFile) {
                projects.add(Project(
                    name = directory.name,
                    image = BitmapFactory.decodeFile(originFile.absolutePath)
                ))
            }
        }
    }

    return projects.reversed()
}