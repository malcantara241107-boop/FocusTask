package com.example.focustask.data.model

// Define que datos contiene una tarea
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: Int,
    val dueDate: String?,
    val isCompleted: Boolean = false
)
