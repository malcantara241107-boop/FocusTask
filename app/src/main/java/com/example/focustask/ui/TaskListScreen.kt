
package com.example.focustask.ui

// Importaciones de Paquetes
import com.example.focustask.viewmodel.TaskDateFilter
import com.example.focustask.viewmodel.TaskPriorityFilter

// Colores Principales
import com.example.focustask.ui.theme.NavyBackground
import com.example.focustask.ui.theme.Turquoise

import android.widget.Toast
import com.example.focustask.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focustask.data.model.Task
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import com.example.focustask.ui.theme.PlusJakartaSansFamily
import com.example.focustask.ui.theme.QuicksandFamily
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.FilterQuality
// Conexión con ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focustask.viewmodel.TaskListViewModel
import com.example.focustask.viewmodel.TaskStatusFilter
import com.example.focustask.data.local.TaskSeedData.tasks

private val NavyBackground = Color(0xFF101929)
private val NavyHeader = Color(0xFF202235)
private val NavySurface = Color(0xFF202340)
private val NavyBorder = Color(0xFF414666)
private val Turquoise = Color(0xFF40D5C2)
private val MainText = Color(0xFFF5F5FA)
private val SecondaryText = Color(0xFFA0A9BC)

private val PriorityLow = Color(0xFF36BDF2)
private val PriorityNormal = Color(0xFF65F58B)
private val PriorityMedium = Color(0xFFF6D44A)
private val PriorityHigh = Color(0xFFFF625D)

private enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onViewTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFiltersDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var draftCategories by remember {
        mutableStateOf(emptySet<String>())
    }

    var draftStatuses by remember {
        mutableStateOf(emptySet<TaskStatusFilter>())
    }

    var draftDates by remember {
        mutableStateOf(emptySet<TaskDateFilter>())
    }

    var draftPriorities by remember {
        mutableStateOf(emptySet<TaskPriorityFilter>())
    }

    var isHeaderExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val completedCount = uiState.completedCount
    val totalCount = uiState.totalCount
    val progress = uiState.progress
    val visibleTasks = uiState.tasks

    /*
    val visibleTasks = editableTasks.filter { task ->
        val matchesSearch = task.title.contains(
            searchText,
            ignoreCase = true
        )

     */

    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NavyBackground,
        floatingActionButton = {
            FloatingActionButton(

                /* Old
                onClick = {
                    Toast.makeText(
                        context,
                        "El formulario de tareas se implementará después",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                 */
                onClick = onAddTask,
                containerColor = Turquoise,
                contentColor = NavyBackground,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyHeader)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp),
                        /*
                        .background(Color.LightGray),
                         */
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.icon_book_pixel
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "FocusTask",
                    fontFamily = QuicksandFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 23.sp,
                    color = Turquoise
                )
            }

            // EX-lugar del titulo de Sección

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isHeaderExpanded = !isHeaderExpanded
                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Tareas Pendientes",
                        fontFamily = QuicksandFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MainText
                    )

                    Text(
                        text = if (isHeaderExpanded) {
                            "Ocultar herramientas"
                        } else {
                            "Mostrar búsqueda y filtros"
                        },
                        fontFamily = PlusJakartaSansFamily,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                Text(
                    text = if (isHeaderExpanded) "▲" else "▼",
                    fontSize = 18.sp,
                    color = Turquoise
                )
            }

            if (isHeaderExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    /*
                    Text(
                        text = "Organiza tus pendientes.",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 4.dp
                        ),
                        fontSize = 12.sp,
                        color = MainText
                    )
                     */

                    TaskSummary(
                        completed = completedCount,
                        total = totalCount,
                        progress = progress,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        )
                    )

                    SearchField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier.padding(
                            horizontal = 20.dp
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                draftCategories = uiState.selectedCategories
                                draftDates = uiState.selectedDates
                                draftPriorities = uiState.selectedPriorities
                                draftStatuses = uiState.selectedStatuses
                                showFiltersDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Turquoise
                            )
                        ) {
                            Text(
                                text = "⚙ Filtros",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        val activeFilters = buildList<Pair<String, () -> Unit>> {
                            uiState.selectedCategories.forEach { category ->
                                add(category to {
                                    viewModel.setCategories(
                                        uiState.selectedCategories - category
                                    )
                                })
                            }

                            uiState.selectedStatuses.forEach { status ->
                                val label = when (status) {
                                    TaskStatusFilter.ALL -> "Todos los estados"
                                    TaskStatusFilter.PENDING -> "Pendientes"
                                    TaskStatusFilter.COMPLETED -> "Completadas"
                                }

                                if (status != TaskStatusFilter.ALL) {
                                    add(label to {
                                        viewModel.setStatusFilters(
                                            uiState.selectedStatuses - status
                                        )
                                    })
                                }
                            }

                            uiState.selectedDates.forEach { date ->
                                val label = when (date) {
                                    TaskDateFilter.ANY -> "Cualquier fecha"
                                    TaskDateFilter.TODAY -> "Hoy"
                                    TaskDateFilter.TOMORROW -> "Mañana"
                                    TaskDateFilter.THIS_WEEK -> "Esta semana"
                                    TaskDateFilter.THIS_MONTH -> "Este mes"
                                    TaskDateFilter.NO_DATE -> "Sin fecha"
                                }

                                if (date != TaskDateFilter.ANY) {
                                    add(label to {
                                        viewModel.setDateFilters(
                                            uiState.selectedDates - date
                                        )
                                    })
                                }
                            }

                            uiState.selectedPriorities.forEach { priority ->
                                val label = when (priority) {
                                    TaskPriorityFilter.ALL -> "Todas las prioridades"
                                    TaskPriorityFilter.LOW -> "Baja"
                                    TaskPriorityFilter.NORMAL -> "Normal"
                                    TaskPriorityFilter.MEDIUM -> "Media"
                                    TaskPriorityFilter.HIGH -> "Alta"
                                }

                                if (priority != TaskPriorityFilter.ALL) {
                                    add(label to {
                                        viewModel.setPriorityFilters(
                                            uiState.selectedPriorities - priority
                                        )
                                    })
                                }
                            }
                        }

                        if (activeFilters.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                activeFilters.forEach { (label, onRemove) ->
                                    ActiveFilterChip(
                                        text = label,
                                        onRemove = onRemove
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sección Inferior

            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(40.dp)
                        .background(Color(0xFF9BBEFF))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF293A54),
                                    NavyBackground
                                )
                            )
                        )
                        .padding(
                            start = 16.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.icon_checklist_white
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Tareas Pendientes",
                            fontFamily = QuicksandFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MainText
                        )
                    }
                }
            }

            Text(
                text = "     Organiza tus pendientes.",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 12.sp,
                color = MainText
            )
             */

            if (visibleTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "No se encontraron tareas"
                            } else {
                                "¡Sin tareas por aquí!"
                            },
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MainText,
                            fontSize = 20.sp,
                        )

                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "Prueba con otro título."
                            } else {
                                "Las tareas aparecerán aquí."
                            },
                            color = Turquoise,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = visibleTasks,
                        key = { it.id }
                    ) { task ->

                        TaskCard(
                            task = task,
                            onCompletedChange = { isCompleted ->
                                viewModel.toggleTaskCompletion(
                                    task.id,
                                    isCompleted
                                )
                            },
                            onEdit = {
                                onEditTask(task)
                            },
                            onDelete = {
                                onDeleteTask(task)
                            },
                            onClick = {
                                onViewTask(task)
                            }
                        )

                    }

                    item {
                        Spacer(Modifier.height(76.dp))
                    }
                }
            }
        }
    }

    if (showFiltersDialog) {
        AlertDialog(
            onDismissRequest = {
                showFiltersDialog = false
            },
            title = {
                Text("Filtros de tareas")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Categoría
                    Text(
                        text = "Categoría",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = draftCategories.isEmpty(),
                            onCheckedChange = {
                                draftCategories = emptySet()
                            }
                        )
                        Text("Todas las categorías")
                    }

                    listOf(
                        "Universidad",
                        "Estudio",
                        "Proyecto",
                        "Personal"
                    ).forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = category in draftCategories,
                                onCheckedChange = { checked ->
                                    draftCategories =
                                        if (checked) {
                                            draftCategories + category
                                        } else {
                                            draftCategories - category
                                        }
                                }
                            )
                            Text(category)
                        }
                    }

                    HorizontalDivider(color = SecondaryText)

                    // Estado
                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = draftStatuses.isEmpty(),
                            onCheckedChange = {
                                draftStatuses = emptySet()
                            }
                        )
                        Text("Todos los estados")
                    }

                    listOf(
                        TaskStatusFilter.PENDING to "Pendientes",
                        TaskStatusFilter.COMPLETED to "Completadas"
                    ).forEach { (status, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = status in draftStatuses,
                                onCheckedChange = { checked ->
                                    draftStatuses =
                                        if (checked) {
                                            draftStatuses + status
                                        } else {
                                            draftStatuses - status
                                        }
                                }
                            )
                            Text(label)
                        }
                    }

                    HorizontalDivider(color = SecondaryText)

                    // Fecha
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = draftDates.isEmpty(),
                            onCheckedChange = {
                                draftDates = emptySet()
                            }
                        )
                        Text("Cualquier fecha")
                    }

                    listOf(
                        TaskDateFilter.TODAY to "Hoy",
                        TaskDateFilter.TOMORROW to "Mañana",
                        TaskDateFilter.THIS_WEEK to "Esta semana",
                        TaskDateFilter.THIS_MONTH to "Este mes",
                        TaskDateFilter.NO_DATE to "Sin fecha"
                    ).forEach { (date, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = date in draftDates,
                                onCheckedChange = { checked ->
                                    draftDates =
                                        if (checked) {
                                            draftDates + date
                                        } else {
                                            draftDates - date
                                        }
                                }
                            )
                            Text(label)
                        }
                    }

                    HorizontalDivider(color = SecondaryText)

                    // Prioridad
                    Text(
                        text = "Prioridad",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = draftPriorities.isEmpty(),
                            onCheckedChange = {
                                draftPriorities = emptySet()
                            }
                        )
                        Text("Todas las prioridades")
                    }

                    listOf(
                        TaskPriorityFilter.LOW to "Baja",
                        TaskPriorityFilter.NORMAL to "Normal",
                        TaskPriorityFilter.MEDIUM to "Media",
                        TaskPriorityFilter.HIGH to "Alta"
                    ).forEach { (priority, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = priority in draftPriorities,
                                onCheckedChange = { checked ->
                                    draftPriorities =
                                        if (checked) {
                                            draftPriorities + priority
                                        } else {
                                            draftPriorities - priority
                                        }
                                }
                            )
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setCategories(draftCategories)
                        viewModel.setStatusFilters(draftStatuses)
                        viewModel.setDateFilters(draftDates)
                        viewModel.setPriorityFilters(draftPriorities)

                        showFiltersDialog = false
                    }
                ) {
                    Text(
                        text = "Aplicar filtros",
                        color = Turquoise
                    )
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            draftCategories = emptySet()
                            draftStatuses = emptySet()
                            draftDates = emptySet()
                            draftPriorities = emptySet()

                            viewModel.setCategories(emptySet())
                            viewModel.setStatusFilters(emptySet())
                            viewModel.setDateFilters(emptySet())
                            viewModel.setPriorityFilters(emptySet())

                            showFiltersDialog = false
                        }
                    ) {
                        Text(
                            text = "Limpiar filtros",
                            color = SecondaryText
                        )
                    }

                    TextButton(
                        onClick = {
                            showFiltersDialog = false
                        }
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Turquoise
                        )
                    }
                }
            },
            containerColor = NavyBackground,
            titleContentColor = MainText,
            textContentColor = SecondaryText
        )
    }
}

@Composable
private fun TaskSummary(
    completed: Int,
    total: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progreso", color = MainText, fontSize = 14.sp)

                Text(
                    text = if (total == 0) {
                        "N/A"
                    } else {
                        "${(progress * 100).toInt()}%"
                    },
                    color = MainText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = Turquoise,
                trackColor = NavyBorder
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (total == 0) {
                    "No hay tareas de momento"
                } else {
                    "$completed de $total tareas completadas"
                },
                color = SecondaryText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NavySurface, RoundedCornerShape(7.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⌕", color = MainText, fontSize = 21.sp)

        Spacer(Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = "Buscar tareas...",
                    color = SecondaryText,
                    fontSize = 13.sp
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = MainText,
                    fontSize = 13.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text,
                fontSize = 12.sp,
                fontFamily = QuicksandFamily,
                fontWeight = FontWeight.Normal
            )
        },
        shape = RoundedCornerShape(7.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Turquoise,
            selectedLabelColor = NavyBackground,
            containerColor = NavySurface,
            labelColor = MainText
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MainText else NavyBorder
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveFilterChip(
    text: String,
    onRemove: () -> Unit
) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text(
                text = "$text  ×",
                fontSize = 12.sp,
                fontFamily = QuicksandFamily,
                fontWeight = FontWeight.Normal
            )
        },
        shape = RoundedCornerShape(7.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Turquoise,
            selectedLabelColor = NavyBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MainText
        )
    )
}

@Composable
private fun TaskCard(
    task: Task,
    onCompletedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        0 -> PriorityLow
        1 -> PriorityNormal
        2 -> PriorityMedium
        3 -> PriorityHigh
        else -> SecondaryText
    }

    val priorityName = when (task.priority) {
        0 -> "Baja"
        1 -> "Normal"
        2 -> "Media"
        3 -> "Alta"
        else -> "Sin definir"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                Color(0xFF1D292F)
            } else {
                NavySurface
            }
        ),
        border = BorderStroke(
            width = if (task.isCompleted) 2.dp else 1.5.dp,
            color = if (task.isCompleted) {
                PriorityNormal
            } else {
                NavyBorder
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCompletedChange,
                modifier = Modifier.size(34.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Turquoise,
                    uncheckedColor = NavyBorder,
                    checkmarkColor = NavyBackground
                )
            )

            Spacer(Modifier.width(5.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) {
                        SecondaryText
                    } else {
                        MainText
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Prioridad: ",
                        color = MainText,
                        fontSize = 12.sp
                    )

                    Text(
                        text = priorityName,
                        color = priorityColor,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = task.description,
                    color = SecondaryText,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = task.category,
                    color = Turquoise,
                    fontSize = 10.sp
                )

                if (task.dueDate != null) {
                    // Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◷ ",
                            color = Turquoise,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Vence: ${task.dueDate}",
                            color = Turquoise,
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }

                /* Column Version
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar tarea",
                            tint = Turquoise
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar tarea",
                            tint = PriorityHigh
                        )
                    }
                }
                */

                /* Version Vertical
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar tarea",
                            tint = Turquoise
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar tarea",
                            tint = PriorityHigh
                        )
                    }
                }
                 */

            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar tarea",
                        tint = Turquoise
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = PriorityHigh
                    )
                }
            }
        }
    }
}
