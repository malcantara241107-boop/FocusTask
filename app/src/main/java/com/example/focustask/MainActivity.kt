package com.example.focustask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.focustask.data.local.TaskLocalDataSource
import com.example.focustask.data.repository.TaskRepositoryImpl
import com.example.focustask.ui.TaskListScreen
import com.example.focustask.ui.theme.FocusTaskTheme
import com.example.focustask.viewmodel.TaskListViewModel
import com.example.focustask.viewmodel.TaskListViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(
            TaskRepositoryImpl(
                TaskLocalDataSource(applicationContext)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FocusTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
