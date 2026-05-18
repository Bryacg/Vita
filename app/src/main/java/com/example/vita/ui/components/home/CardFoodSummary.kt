package com.example.vita.ui.components.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// ─── Formulario para registrar una comida ─────────────────────────────────────

private data class OpcionSalud(
    val etiqueta: String,
    val puntos: Int,
    val color: Color
)

private val opcionesSalud = listOf(
    OpcionSalud("Muy saludable", 90, Color(0xFF2E7D32)),
    OpcionSalud("Saludable",     70, Color(0xFF4CAF50)),
    OpcionSalud("Regular",       50, Color(0xFFF2994A)),
    OpcionSalud("Poco sano",     20, Color(0xFFC62828))
)

@Composable
fun CardAddMealForm(
    // onSave ahora recibe también el healthyScore elegido por el usuario
    onSave: (nombre: String, kcal: Int, healthScore: Int) -> Unit,
    onCancel: () -> Unit
) {
    var foodName      by remember { mutableStateOf("") }
    var calories      by remember { mutableStateOf("") }
    var selectedScore by remember { mutableStateOf(70) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text  = "Registrar Alimento",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value         = foodName,
                onValueChange = { foodName = it },
                label         = { Text("¿Qué comiste?") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = calories,
                onValueChange = { if (it.all { c -> c.isDigit() }) calories = it },
                label         = { Text("Calorías (kcal)") },
                modifier      = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine    = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text  = "Calidad nutricional",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Chips de calidad — el usuario elige cuán saludable es la comida
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                opcionesSalud.forEach { opcion ->
                    val seleccionado = selectedScore == opcion.puntos
                    FilterChip(
                        selected = seleccionado,
                        onClick  = { selectedScore = opcion.puntos },
                        label    = {
                            Text(
                                text  = opcion.etiqueta,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = opcion.color.copy(alpha = 0.2f),
                            selectedLabelColor     = opcion.color
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick  = {
                        val kcal = calories.toIntOrNull() ?: return@Button
                        if (foodName.isNotBlank() && kcal > 0) {
                            onSave(foodName, kcal, selectedScore)
                        }
                    },
                    enabled  = foodName.isNotBlank() && calories.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

// ─── Resumen del día ──────────────────────────────────────────────────────────

@Composable
fun CardFoodSummary(
    totalCalories: Int,
    averageHealthScore: Int,
    onAddClick: () -> Unit
) {
    val healthColor = when {
        averageHealthScore >= 70 -> Color(0xFF2E7D32)
        averageHealthScore >= 40 -> Color(0xFFF2994A)
        averageHealthScore > 0   -> Color(0xFFC62828)
        else                     -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ElevatedCard(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f),
                CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "Nutrición de hoy",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "$totalCalories kcal consumidas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (averageHealthScore > 0) {
                    Surface(
                        color = healthColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier            = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text       = "$averageHealthScore",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = healthColor
                            )
                            Text(
                                text  = "puntos",
                                style = MaterialTheme.typography.labelSmall,
                                color = healthColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick  = onAddClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar comida")
            }
        }
    }
}