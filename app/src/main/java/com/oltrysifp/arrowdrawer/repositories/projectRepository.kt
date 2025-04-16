package com.oltrysifp.arrowdrawer.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineDto
import com.oltrysifp.arrowdrawer.models.LineSettings
import com.oltrysifp.arrowdrawer.models.Project
import com.oltrysifp.arrowdrawer.models.ProjectMeta
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

class ProjectRepository(context: Context) {
    private val projectsDir = File(context.getExternalFilesDir(null), "projects")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        if (!projectsDir.exists()) {
            projectsDir.mkdirs()
        }
    }

    fun saveProject(project: Project): Boolean {
        return try {
            val projectDir = File(projectsDir, project.name).apply { mkdirs() }

            // Save image to a temp file
            val tempImageFile = File(projectDir, "origin_temp.png")
            FileOutputStream(tempImageFile).use { stream ->
                project.image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            // Replace original file only after successful write
            val imageFile = File(projectDir, "origin.png")
            if (tempImageFile.renameTo(imageFile).not()) {
                tempImageFile.delete() // Cleanup if replace fails
                return false
            }

            // Save JSON to a temp file
            val tempObjectsFile = File(projectDir, "objects_temp.json")
            tempObjectsFile.writeText(
                json.encodeToString(project.objects.map { it.toDto() })
            )
            val objectsFile = File(projectDir, "objects.json")
            if (tempObjectsFile.renameTo(objectsFile).not()) {
                tempObjectsFile.delete()
                return false
            }

            val metaFile = File(projectDir, "meta.json")
            metaFile.writeText(
                json.encodeToString(ProjectMeta(project.lastEdit, project.settings))
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadProject(name: String): Project? {
        return try {
            val projectDir = File(projectsDir, name)
            if (!projectDir.exists()) return null

            val imageFile = File(projectDir, "origin.png")
            val image = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return null

            val objectsFile = File(projectDir, "objects.json")
            val objects = json.decodeFromString<List<LineDto>>(objectsFile.readText())
                .map { Line.fromDto(it) }

            val metaFile = File(projectDir, "meta.json")
            val projectMeta = if (metaFile.exists()) {
                json.decodeFromString<ProjectMeta>(metaFile.readText())
            } else {
                ProjectMeta(0L, LineSettings())
            }

            Project(name, image, objects, projectMeta.lastEdit, projectMeta.settings)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun listProjects(): List<Project> {
        return projectsDir.listFiles()
            ?.filter { it.isDirectory && File(it, "objects.json").exists() }
            ?.mapNotNull { loadProject(it.name) }
            ?: emptyList()
    }

    fun deleteProject(name: String): Boolean {
        return try {
            File(projectsDir, name).deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun renameProject(oldName: String, newName: String): Boolean {
        return try {
            val oldDir = File(projectsDir, oldName)
            val newDir = File(projectsDir, newName)

            if (!oldDir.exists() || newDir.exists()) {
                // Can't rename if old doesn't exist or new already exists
                return false
            }

            oldDir.renameTo(newDir)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}