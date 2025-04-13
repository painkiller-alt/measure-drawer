package com.oltrysifp.arrowdrawer.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oltrysifp.arrowdrawer.models.Line
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

    private val saveTrigger = MutableStateFlow(false)

    init {
        loadProjectsList()

        // Debounced save handler
        viewModelScope.launch {
            saveTrigger.collectLatest {
                withContext(Dispatchers.IO) {
                    _currentProject.value?.let { project ->
                        val updatedProject = project.copy(objects = _currentLines.value)
                        repository.saveProject(updatedProject)
                        saveTrigger.value = false
                    }
                }
            }
        }
    }

    fun createProject(project: Project) {
        viewModelScope.launch {
            if (repository.saveProject(project)) {
                loadProjectsList()
                _currentProject.value = project
            }
        }
    }

    fun loadProject(name: String) {
        viewModelScope.launch {
            repository.loadProject(name)?.let { loadedProject ->
                _currentProject.value = loadedProject
                _currentLines.value = loadedProject.objects
            }
        }
    }

    fun forceSave() {
        CoroutineScope(Dispatchers.IO).launch {
            _currentProject.value?.let { project ->
                val updatedProject = project.copy(objects = _currentLines.value)
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

    fun updateLines(lines: List<Line>) {
        _currentLines.value = lines
    }

    fun triggerSave() {
        saveTrigger.value = true
    }

    private fun loadProjectsList() {
        viewModelScope.launch {
            _projects.value = repository.listProjects()
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