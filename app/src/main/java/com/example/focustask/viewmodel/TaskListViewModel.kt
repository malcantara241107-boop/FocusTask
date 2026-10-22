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
            .filter { task ->
                when (state.selectedPriority) {
                    TaskPriorityFilter.ALL -> true
                    TaskPriorityFilter.LOW -> task.priority == 0
                    TaskPriorityFilter.NORMAL -> task.priority == 1
                    TaskPriorityFilter.MEDIUM -> task.priority == 2
                    TaskPriorityFilter.HIGH -> task.priority == 3
                }
            }
            .filter { task ->
                val today = LocalDate.now()
                val taskDate = task.dueDate?.let {
                    LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                }

                when (state.selectedDate) {
                    TaskDateFilter.ANY -> true
                    TaskDateFilter.NO_DATE -> taskDate == null
                    TaskDateFilter.TODAY -> taskDate == today
                    TaskDateFilter.TOMORROW -> taskDate == today.plusDays(1)
                    TaskDateFilter.THIS_WEEK -> taskDate != null &&
                            !taskDate.isBefore(today.with(DayOfWeek.MONDAY)) &&
                            !taskDate.isAfter(today.with(DayOfWeek.MONDAY).plusDays(6))
                    TaskDateFilter.THIS_MONTH -> taskDate != null &&
                            taskDate.year == today.year &&
                            taskDate.month == today.month
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

    fun setDateFilter(date: TaskDateFilter) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }

    fun setPriorityFilter(priority: TaskPriorityFilter) {
        _uiState.update {
            it.copy(selectedPriority = priority)
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

