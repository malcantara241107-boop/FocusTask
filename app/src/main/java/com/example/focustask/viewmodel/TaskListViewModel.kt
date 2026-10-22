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
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

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
                state.selectedCategories.isEmpty() ||
                        task.category in state.selectedCategories
            }
            .filter { task ->
                state.selectedStatuses.isEmpty() ||
                        state.selectedStatuses.any { status ->
                            when (status) {
                                TaskStatusFilter.ALL -> true
                                TaskStatusFilter.PENDING -> !task.isCompleted
                                TaskStatusFilter.COMPLETED -> task.isCompleted
                            }
                        }
            }
            .filter { task ->
                state.selectedPriorities.isEmpty() ||
                        state.selectedPriorities.any { priority ->
                            when (priority) {
                                TaskPriorityFilter.ALL -> true
                                TaskPriorityFilter.LOW -> task.priority == 0
                                TaskPriorityFilter.NORMAL -> task.priority == 1
                                TaskPriorityFilter.MEDIUM -> task.priority == 2
                                TaskPriorityFilter.HIGH -> task.priority == 3
                            }
                        }
            }
            .filter { task ->
                val today = java.time.LocalDate.now()
                val taskDate = task.dueDate?.let {
                    runCatching { java.time.LocalDate.parse(it) }.getOrNull()
                }

                state.selectedDates.isEmpty() ||
                        state.selectedDates.any { date ->
                            when (date) {
                                TaskDateFilter.ANY -> true
                                TaskDateFilter.NO_DATE -> taskDate == null
                                TaskDateFilter.TODAY -> taskDate == today
                                TaskDateFilter.TOMORROW -> taskDate == today.plusDays(1)
                                TaskDateFilter.THIS_WEEK -> {
                                    val monday = today.with(
                                        java.time.temporal.TemporalAdjusters
                                            .previousOrSame(java.time.DayOfWeek.MONDAY)
                                    )
                                    taskDate != null &&
                                            !taskDate.isBefore(monday) &&
                                            !taskDate.isAfter(monday.plusDays(6))
                                }
                                TaskDateFilter.THIS_MONTH -> {
                                    taskDate != null &&
                                            taskDate.year == today.year &&
                                            taskDate.month == today.month
                                }
                            }
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

    fun setCategories(categories: Set<String>) {
        _uiState.update {
            it.copy(selectedCategories = categories)
        }
    }

    fun setStatusFilters(statuses: Set<TaskStatusFilter>) {
        _uiState.update {
            it.copy(selectedStatuses = statuses)
        }
    }

    fun setSortOption(option: TaskSortOption) {
        _uiState.update { it.copy(sortOption = option) }
    }

    fun setDateFilters(dates: Set<TaskDateFilter>) {
        _uiState.update {
            it.copy(selectedDates = dates)
        }
    }

    fun setPriorityFilters(priorities: Set<TaskPriorityFilter>) {
        _uiState.update {
            it.copy(selectedPriorities = priorities)
        }
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

