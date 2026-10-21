// Implementa el contrato de TaskRepository y se encarga de agregar, actualizar y
// eliminar tareas sin que el resto de la aplicación tenga que manipular
// directamente la lista guardada.
package com.example.focustask.data.repository

import com.example.focustask.data.local.TaskLocalDataSource
import com.example.focustask.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource
) : TaskRepository {

    override val tasks: Flow<List<Task>> =
        localDataSource.tasks

    override suspend fun addTask(task: Task) {
        val currentTasks = localDataSource.tasks.first()

        localDataSource.saveTasks(
            currentTasks + task
        )
    }

    override suspend fun updateTask(task: Task) {
        val currentTasks = localDataSource.tasks.first()

        val updatedTasks = currentTasks.map { currentTask ->
            if (currentTask.id == task.id) {
                task
            } else {
                currentTask
            }
        }

        localDataSource.saveTasks(updatedTasks)
    }

    override suspend fun deleteTask(taskId: String) {
        val currentTasks = localDataSource.tasks.first()

        val updatedTasks = currentTasks.filterNot { task ->
            task.id == taskId
        }

        localDataSource.saveTasks(updatedTasks)
    }


    override suspend fun initializeIfNeeded(seedTasks: List<Task>) {
        localDataSource.initializeIfNeeded(seedTasks)
    }

}
