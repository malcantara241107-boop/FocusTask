package com.example.focustask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustask.data.model.Task
import com.example.focustask.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.focustask.data.local.TaskSeedData

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded(TaskSeedData.tasks)
            observeTasks()
        }
    }

    private suspend fun observeTasks() {
        combine(repository.tasks, _uiState) { allTasks, state ->
            val filteredTasks = applyFiltersAndSort(
                tasks = allTasks,
                state = state
            )

            state.copy(
                allTasks = allTasks,
                tasks = filteredTasks,
                isLoading = false,
                errorMessage = null
            )
        }.catch { exception ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "No se pudieron cargar las tareas."
                )
            }
        }.collect { newState ->
            _uiState.value = newState
        }
    }



    private fun applyFiltersAndSort(
        tasks: List<Task>,
        state: TaskListUiState
    ): List<Task> {
        return tasks
            .filter { task ->
                task.title.contains(
                    state.searchQuery,
                    ignoreCase = true
                )
            }
            .filter { task ->
                state.selectedCategory == null ||
                        task.category == state.selectedCategory
            }
            .filter { task ->
                when (state.selectedStatus) {
                    TaskStatusFilter.ALL -> true
                    TaskStatusFilter.PENDING -> !task.isCompleted
                    TaskStatusFilter.COMPLETED -> task.isCompleted
                }
            }
            .let { filteredTasks ->
                when (state.sortOption) {
                    TaskSortOption.PRIORITY ->
                        filteredTasks.sortedByDescending { it.priority }

                    TaskSortOption.DUE_DATE ->
                        filteredTasks.sortedBy { it.dueDate ?: "9999-12-31" }
                }
            }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setStatusFilter(status: TaskStatusFilter) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun setSortOption(option: TaskSortOption) {
        _uiState.update { it.copy(sortOption = option) }
    }

    /*
    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val currentTask = repository.tasks
                .let { flow -> flow }
                .collect { tasks ->
                    tasks.find { it.id == taskId }?.let { task ->
                        repository.updateTask(
                            task.copy(isCompleted = isCompleted)
                        )
                    }
                    return@collect
                }
        }
    }
     */

    // Obtiene la tarea una sola vez
    fun toggleTaskCompletion(
        taskId: String,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            val tasks = repository.tasks.first()
            val task = tasks.find { it.id == taskId }
                ?: return@launch

            repository.updateTask(
                task.copy(isCompleted = isCompleted)
            )
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}

