package com.example.focustask.ui

// Temas
import com.example.focustask.ui.theme.NavyBackground
import com.example.focustask.ui.theme.MainText
import com.example.focustask.ui.theme.Turquoise
import com.example.focustask.ui.theme.SecondaryText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focustask.data.model.Task
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


@Composable
fun TaskDetailScreen(
    task: Task,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

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
                text = "Detalles de Tarea",
                modifier = Modifier.weight(1f),
                color = MainText,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tarea",
                    tint = Turquoise
                )
            }
        }

        val priorityColor = when (task.priority) {
            0 -> androidx.compose.ui.graphics.Color(0xFF9BBEFF) // Baja
            1 -> androidx.compose.ui.graphics.Color(0xFF9FE3B0) // Normal
            2 -> androidx.compose.ui.graphics.Color(0xFFFFD166) // Media
            3 -> androidx.compose.ui.graphics.Color(0xFFFF7777) // Alta
            else -> SecondaryText
        }

        val priorityName = when (task.priority) {
            0 -> "BAJA"
            1 -> "NORMAL"
            2 -> "MEDIA"
            3 -> "ALTA"
            else -> "Sin prioridad"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(40.dp)
                    .background(priorityColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color(0xFF293A54),
                                NavyBackground
                            )
                        )
                    )
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
            ) {
                Text(
                    text = "PRIORIDAD",
                    fontSize = 10.sp,
                    color = SecondaryText
                )

                Text(
                    text = priorityName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = priorityColor
                )
            }
        }


        Text(
            text = task.title,
            color = MainText,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        val statusText = if (task.isCompleted) {
            "Completada"
        } else {
            "Pendiente"
        }

        Surface(
            color = if (task.isCompleted) {
                androidx.compose.ui.graphics.Color(0xFF285C4D)
            } else {
                androidx.compose.ui.graphics.Color(0xFF293A54)
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = statusText,
                color = MainText,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                fontSize = 13.sp
            )
        }

        Text(
            text = "Descripción",
            color = Turquoise,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )


        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = NavyBackground,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                androidx.compose.ui.graphics.Color(0xFF455166)
            )
        ) {
            Text(
                text = task.description.ifBlank {
                    "Esta tarea no tiene descripción."
                },
                modifier = Modifier.padding(12.dp),
                color = MainText,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        // Versión Scrolleable
        /*
        Text(
            text = task.description.ifBlank {
                "Esta tarea no tiene descripción."
            },
            modifier = Modifier
                .padding(12.dp)
                .heightIn(max = 220.dp)
                .verticalScroll(rememberScrollState()),
            color = MainText,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
         */


        Text(
            text = "Fecha de entrega",
            color = Turquoise,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = task.dueDate?.takeIf { it.isNotBlank() }
                ?: "Sin fecha de entrega",
            color = MainText,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Descartado
            /*
            Surface(
                color = androidx.compose.ui.graphics.Color(0xFF293A54),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when (task.priority) {
                        0 -> "Prioridad baja"
                        1 -> "Prioridad normal"
                        2 -> "Prioridad media"
                        3 -> "Prioridad alta"
                        else -> "Sin prioridad"
                    },
                    color = MainText,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 13.sp
                )
            }
             */

            Surface(
                color = androidx.compose.ui.graphics.Color(0xFF293A54),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Categoría: ${task.category}",
                    color = Turquoise,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}
