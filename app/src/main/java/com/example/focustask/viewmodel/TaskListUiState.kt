package com.example.focustask.viewmodel

import com.example.focustask.data.model.Task

enum class TaskStatusFilter {
    ALL, PENDING, COMPLETED
}

enum class TaskSortOption {
    PRIORITY, DUE_DATE
}

data class TaskListUiState(
    val allTasks: List<Task> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedStatus: TaskStatusFilter = TaskStatusFilter.ALL,
    val sortOption: TaskSortOption = TaskSortOption.DUE_DATE,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val completedCount: Int
        get() = allTasks.count { it.isCompleted }

    val totalCount: Int
        get() = allTasks.size

    val progress: Float
        get() = if (totalCount == 0) {
            0f
        } else {
            completedCount.toFloat() / totalCount
        }
}