package com.example.focustask

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.focustask.data.model.Task
import com.example.focustask.ui.AddTaskScreen
import com.example.focustask.ui.TaskListScreen
import com.example.focustask.viewmodel.TaskListViewModel

import java.util.UUID

@Composable
fun AppNavigation(
    viewModel: TaskListViewModel
) {
    val navController = rememberNavController()

    var selectedTask by remember {
        mutableStateOf<Task?>(null)
    }

    var taskToDelete by remember {
        mutableStateOf<Task?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "task_list"
    ) {
        composable("task_list") {
            TaskListScreen(
                viewModel = viewModel,

                onAddTask = {
                    navController.navigate("add_task")
                },

                onEditTask = { task ->
                    selectedTask = task
                    navController.navigate("edit_task")
                },

                onDeleteTask = { task ->
                    taskToDelete = task
                }
            )
        }

        // Ruta exclusiva para crear tareas.
        composable("add_task") {
            AddTaskScreen(
                taskToEdit = null,
                isEditing = false,

                onBack = {
                    navController.popBackStack()
                },

                onSaveTask = {
                        title,
                        description,
                        category,
                        priority,
                        dueDate ->

                    val newTask = Task(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = dueDate,
                        isCompleted = false
                    )

                    viewModel.addTask(newTask)
                    navController.popBackStack()
                }
            )
        }

        // Ruta exclusiva para editar tareas existentes.
        composable("edit_task") {
            val task = selectedTask

            if (task != null) {
                AddTaskScreen(
                    taskToEdit = task,
                    isEditing = true,

                    onBack = {
                        navController.popBackStack()
                    },

                    onSaveTask = {
                            title,
                            description,
                            category,
                            priority,
                            dueDate ->

                        val updatedTask = task.copy(
                            title = title,
                            description = description,
                            category = category,
                            priority = priority,
                            dueDate = dueDate
                        )

                        viewModel.updateTask(updatedTask)
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    // Confirmación antes de eliminar una tarea.
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },

            title = {
                Text("Eliminar tarea")
            },

            text = {
                Text(
                    "¿Seguro que deseas eliminar " +
                            "\"${taskToDelete?.title}\"?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        val task = taskToDelete

                        if (task != null) {
                            viewModel.deleteTask(task.id)
                        }

                        taskToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
