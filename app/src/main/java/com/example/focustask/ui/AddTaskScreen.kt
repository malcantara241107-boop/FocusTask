package com.example.focustask.ui

// Colores Principales
import com.example.focustask.ui.theme.NavyBackground
import com.example.focustask.ui.theme.Turquoise
import com.example.focustask.ui.theme.PriorityLow
import com.example.focustask.ui.theme.PriorityNormal
import com.example.focustask.ui.theme.PriorityMedium
import com.example.focustask.ui.theme.PriorityHigh

// Importaciones del Proyecto
import com.example.focustask.data.model.Task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import android.app.DatePickerDialog
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import com.example.focustask.ui.theme.NavyHeader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.mutableIntStateOf
import com.example.focustask.ui.theme.MainText
import com.example.focustask.ui.theme.NavyBackground
import com.example.focustask.ui.theme.NavyBorder
import com.example.focustask.ui.theme.NavySurface
import com.example.focustask.ui.theme.SecondaryText
import com.example.focustask.ui.theme.Turquoise

@Composable
fun AddTaskScreen(
    onBack: () -> Unit,
    onSaveTask: (
        String, String, String, Int, String?
            ) -> Unit,
    taskToEdit: Task? = null,
    isEditing: Boolean = taskToEdit != null,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable(taskToEdit?.id) {
        mutableStateOf(taskToEdit?.title ?: "")
    }

    var description by rememberSaveable(taskToEdit?.id) {
        mutableStateOf(taskToEdit?.description ?: "")
    }

    var priority by rememberSaveable(taskToEdit?.id) {
        mutableIntStateOf(taskToEdit?.priority ?: 0)
    }

    var category by rememberSaveable(taskToEdit?.id) {
        mutableStateOf(taskToEdit?.category ?: "Personal")
    }

    var dueDate by rememberSaveable(taskToEdit?.id) {
        mutableStateOf(taskToEdit?.dueDate)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // HEADER FIJO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MainText
                )
            }

            Text(
                if (isEditing) {
                    "Editar Tarea"
                } else {
                    "Añadir Tarea"
                },
                modifier = Modifier.weight(1f),
                color = MainText,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        // FORMULARIO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /*
            Text(
                text = "Añadir Tarea",
                color = MainText,
                fontSize = 28.sp
            )
             */

            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelected = { priority = it }
            )

            Text(
                text = "Título *",
                color = MainText
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Nombre de la tarea")
                },
                singleLine = true,
                colors = taskFieldColors()
            )

            Text(
                text = "Descripción",
                color = MainText
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Detalles adicionales...")
                },
                minLines = 3,
                colors = taskFieldColors()
            )

            CategorySelector(
                selectedCategory = category,
                onCategorySelected = { category = it }
            )


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Fecha de vencimiento (opcional)",
                    color = MainText
                )

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()

                        if (dueDate != null) {
                            val parts = dueDate!!.split("-")
                            calendar.set(
                                parts[0].toInt(),
                                parts[1].toInt() - 1,
                                parts[2].toInt()
                            )
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dueDate = String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    year,
                                    month + 1,
                                    day
                                )
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dueDate ?: "Seleccionar fecha",
                        color = MainText
                    )
                }

                if (dueDate != null) {
                    OutlinedButton(
                        onClick = { dueDate = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quitar fecha",
                            color = SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveTask(
                            title.trim(),
                            description.trim(),
                            category,
                            priority,
                            dueDate
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turquoise,
                    contentColor = NavyBackground
                )
            ) {
                Text(
                    if (isEditing) {
                        "Guardar Cambios"
                    } else {
                        "Agregar Tarea"
                    }
                )
            }
        }
    }
}


@Composable
private fun PrioritySelector(
    selectedPriority: Int,
    onPrioritySelected: (Int) -> Unit
) {
    val priorities = listOf(
        "BAJA" to PriorityLow,
        "NORMAL" to PriorityNormal,
        "MEDIA" to PriorityMedium,
        "ALTA" to PriorityHigh
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Prioridad",
            color = MainText
        )

        priorities.forEachIndexed { index, item ->
            val (label, color) = item

            Button(
                onClick = { onPrioritySelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPriority == index) {
                        color.copy(alpha = 0.25f)
                    } else {
                        NavySurface
                    },
                    contentColor = color
                )
            ) {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
private fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "Universidad",
        "Estudio",
        "Proyecto",
        "Personal"
    )

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Categoría",
            color = MainText
        )

        androidx.compose.material3.OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCategory,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = MainText
            )

            androidx.compose.material3.Text("⌄", color = MainText)

            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun taskFieldColors() =
    androidx.compose.material3.OutlinedTextFieldDefaults.colors(
        focusedTextColor = MainText,
        unfocusedTextColor = MainText,
        focusedBorderColor = Turquoise,
        unfocusedBorderColor = NavyBorder,
        focusedPlaceholderColor = SecondaryText,
        unfocusedPlaceholderColor = SecondaryText,
        cursorColor = Turquoise,
        focusedContainerColor = NavySurface,
        unfocusedContainerColor = NavySurface
    )
