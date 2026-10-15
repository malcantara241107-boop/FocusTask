package com.example.focustask.data.local

import com.example.focustask.data.model.Task

// Definimos los datos de una tarea
object TaskSeedData {

    val tasks = listOf(
        Task(
            id = "task-001",
            title = "Estudiar Kotlin",
            description = "Repasar clases y funciones",
            category = "Universidad",
            priority = 1,
            dueDate = "2026-10-14"
        ),
        Task(
            id = "task-002",
            title = "Investigar APIs",
            description = "Comparar APIs para Apps Web",
            category = "Universidad",
            priority = 2,
            dueDate = "2026-10-15"
        ),
        Task(
            id = "task-003",
            title = "Avanzar en FocusTask",
            description = "Construir el modelo y organizar archivos",
            category = "Proyecto",
            priority = 1,
            dueDate = "2026-10-13"
        ),
        Task(
            id = "task-004",
            title = "Repasar estructura de datos",
            description = "Practicar listas y pilas",
            category = "Estudio",
            priority = 3,
            dueDate = null
        )
    )
}
