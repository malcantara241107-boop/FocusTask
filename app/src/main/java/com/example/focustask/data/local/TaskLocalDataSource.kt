//  Leer las tareas guardadas y exponerlas como un Flow<List<Task>>.
//  Guardar la lista de tareas en formato JSON dentro de DataStore.
//  Inicializar las tareas de ejemplo una sola vez, en el primer inicio de la aplicación.
package com.example.focustask.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.focustask.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

private val Context.taskDataStore by preferencesDataStore(
    name = "focus_task_preferences"
)

class TaskLocalDataSource(context: Context) {

    private val dataStore: DataStore<Preferences> =
        context.applicationContext.taskDataStore

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val tasksKey = stringPreferencesKey("tasks_json")
    private val initializedKey = booleanPreferencesKey("tasks_initialized")

    val tasks: Flow<List<Task>> = dataStore.data
        .map { preferences ->
            val tasksJson = preferences[tasksKey]

            if (tasksJson == null) {
                emptyList()
            } else {
                try {
                    json.decodeFromString<List<Task>>(tasksJson)
                } catch (exception: SerializationException) {
                    emptyList()
                }
            }
        }
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyList())
            } else {
                throw exception
            }
        }

    suspend fun saveTasks(tasks: List<Task>) {
        dataStore.edit { preferences ->
            preferences[tasksKey] = json.encodeToString(tasks)
            preferences[initializedKey] = true
        }
    }

    suspend fun initializeIfNeeded(seedTasks: List<Task>) {
        dataStore.edit { preferences ->
            if (preferences[initializedKey] != true) {
                preferences[tasksKey] = json.encodeToString(seedTasks)
                preferences[initializedKey] = true
            }
        }
    }
}
