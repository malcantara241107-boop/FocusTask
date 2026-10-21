package com.example.focustask.data.repository

import com.example.focustask.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    val tasks: Flow<List<Task>>

    suspend fun initializeIfNeeded(seedTasks: List<Task>)

    suspend fun addTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(taskId: String)

}
