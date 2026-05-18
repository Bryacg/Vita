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

// ─── Formulario de registro simplificado ─────────────────────────────────────

@Composable
fun CardAddMealForm(
    onSave: (nombre: String, kcal: Int) -> Unit,   // sin healthScore manual
    onCancel: () -> Unit
) {
    var foodName  by remember { mutableStateOf("") }
    var calories  by remember { mutableStateOf("") }

    ElevatedCard(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text  = "Registrar alimento",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Aviso de clasificación automática
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text     = "La calidad nutricional se calcula automáticamente.",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

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
                value           = calories,
                onValueChange   = { if (it.all { c -> c.isDigit() }) calories = it },
                label           = { Text("Calorías (kcal)") },
                modifier        = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine      = true,
                supportingText  = {
                    Text(
                        text  = "Más de ${com.example.vita.domain.model.GameConfig.LIMITE_CALORIAS_INGESTA} kcal se clasifica como \"Poco sano\"",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val kcal = calories.toIntOrNull() ?: return@Button
                        if (foodName.isNotBlank() && kcal > 0) {
                            onSave(foodName.trim(), kcal)
                        }
                    },
                    enabled = foodName.isNotBlank() && calories.isNotBlank()
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
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
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