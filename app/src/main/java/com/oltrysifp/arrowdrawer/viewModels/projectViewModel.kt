package com.oltrysifp.arrowdrawer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.LineSettings
import com.oltrysifp.arrowdrawer.models.Project
import com.oltrysifp.arrowdrawer.repositories.ProjectRepository
import com.oltrysifp.arrowdrawer.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject = _currentProject.asStateFlow()

    private val _currentLines = MutableStateFlow<List<Line>>(emptyList())
    val currentLines = _currentLines.asStateFlow()

    private val _canvasSettings = MutableStateFlow(LineSettings())
    val canvasSettings = _canvasSettings.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val saveTrigger = MutableStateFlow(false)

    init {
        loadProjectsList()

        // Debounced save handler
        viewModelScope.launch {
            saveTrigger.collectLatest {
                if (it) {
                    withContext(Dispatchers.IO) {
                        _currentProject.value?.let { project ->
                            val updatedProject = getSaveInstance(project)
                            repository.saveProject(updatedProject)
                            saveTrigger.value = false
                        }
                    }
                }
            }
        }
    }

    fun createProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            if (repository.saveProject(project)) {
                loadProjectsList()
                _currentProject.value = project
            }
            _isSaving.value = false
        }
    }

    fun loadProject(name: String) {
        viewModelScope.launch {
            repository.loadProject(name)?.let { loadedProject ->
                _currentProject.value = loadedProject
                _currentLines.value = loadedProject.objects
                _canvasSettings.value = loadedProject.settings
            }
        }
    }

    fun forceSave() {
        CoroutineScope(Dispatchers.IO).launch {
            _currentProject.value?.let { project ->
                val updatedProject = getSaveInstance(project)
                repository.saveProject(updatedProject)
                log("saved by exit")
            }
        }
    }

    fun deleteProject(name: String) {
        viewModelScope.launch {
            if (repository.deleteProject(name)) {
                loadProjectsList()
                if (_currentProject.value?.name == name) {
                    _currentProject.value = null
                }
            }
        }
    }

    fun renameProject(oldName: String, newName: String) {
        viewModelScope.launch {
            if (repository.renameProject(oldName, newName)) {
                // If the current project is the one being renamed, update it
                _currentProject.value?.let { project ->
                    if (project.name == oldName) {
                        _currentProject.value = project.copy(name = newName)
                    }
                }
                loadProjectsList() // Refresh list of project names
            }
        }
    }

    private fun getSaveInstance(project: Project): Project {
        return project.copy(
            objects = _currentLines.value,
            settings = _canvasSettings.value
        )
    }

    fun updateLines(lines: List<Line>) {
        _currentLines.value = lines
    }

    fun updateCurrentProjectSettings(settings: LineSettings) {
        _canvasSettings.value = settings
    }

    fun triggerSave() {
        saveTrigger.value = true
    }

    private fun loadProjectsList() {
        viewModelScope.launch {
            _projects.value = repository.listProjects()
                .sortedByDescending { it.lastEdit }
        }
    }
}

class ProjectViewModelFactory(private val repository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}