package com.oltrysifp.arrowdrawer.bitmap

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    filename: String,
    folderName: String = "project"
): String? {
    return try {
        // Create "project" directory if it doesn't exist
        val directory = File(context.getExternalFilesDir(null), folderName)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Create file reference
        val file = File(directory, "$filename.png")

        // Compress and save bitmap
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Return the saved file path
        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}