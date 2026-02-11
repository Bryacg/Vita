package com.example.vita.ui.components.retos

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Challenger
import com.example.vita.ui.components.home.LineaBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CardRetosD(
    challenger: Challenger,
    onUpdateClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val isEnabled = challenger.status != "COMPLETED" && challenger.status != "EXPIRED"

    // Definición de color según el estado
    val statusColor = when (challenger.status) {
        "COMPLETED" -> Color(0xFF4CAF50) // Verde
        "PROGRESSO" -> MaterialTheme.colorScheme.primary
        "EXPIRED" -> MaterialTheme.colorScheme.error // Rojo
        else -> Color.Gray
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Fila Superior: Tipo y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text(challenger.type.uppercase(), style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )

                Text(
                    text = challenger.status.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nombre y Descripción
            Column {
                Text(
                    text = challenger.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = challenger.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sección de Progreso
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Mi progreso", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "${challenger.currentValue} / ${challenger.targetValue}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                LineaBar(challenger.targetValue, challenger.currentValue)
            }

            // Fecha de Expiración
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassBottom,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (challenger.status == "EXPIRED") Color.Red else Color.Unspecified
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Límite: ${formatDate(challenger.deadline)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(4.dp))

            // --- BOTÓN DE ACCIÓN CORREGIDO ---
            // Usamos Surface para que el gesto no sea bloqueado por el componente Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .pointerInput(challenger.id, challenger.currentValue) { // <--- AÑADE EL CURRENT VALUE AQUÍ
                        detectTapGestures(
                            onTap = {
                                if (isEnabled) onUpdateClick()
                            },
                            onLongPress = {
                                if (isEnabled) onLongClick()
                            }
                        )
                    },
                shape = MaterialTheme.shapes.extraLarge,
                // Si está deshabilitado se ve gris, si no, usa el color primario
                color = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isEnabled) "Avance (Mantén para completar)" else "Reto Finalizado",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}