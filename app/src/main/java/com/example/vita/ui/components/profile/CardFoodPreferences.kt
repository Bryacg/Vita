package com.example.vita.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference

@Composable
fun CardFoodPreferences(
    preferences: List<Pair<Food, FoodPreference>>, // Lista de alimento + su preferencia
    onAddPreference: (foodName: String, type: String) -> Unit,
    onRemovePreference: (FoodPreference) -> Unit
) {
    var foodInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Gusta") }
    val types = listOf("Gusta", "Disgusta", "Alérgico")

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Preferencias Alimenticias",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registra lo que te gusta, lo que no y tus alergias para personalizar tus retos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario de entrada rápida
            OutlinedTextField(
                value = foodInput,
                onValueChange = { foodInput = it },
                label = { Text("¿Qué alimento?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (foodInput.isNotBlank()) {
                                onAddPreference(foodInput, selectedType)
                                foodInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                }
            )

            // Selector de tipo (Chips)
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when(type) {
                                "Alérgico" -> MaterialTheme.colorScheme.errorContainer
                                "Disgusta" -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Lista de preferencias ya registradas
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                preferences.forEach { (food, pref) ->
                    PreferenceItem(food, pref, onRemovePreference)
                }
            }
        }
    }
}

@Composable
fun PreferenceItem(
    food: Food,
    preference: FoodPreference,
    onRemove: (FoodPreference) -> Unit
) {
    val color = when (preference.preferenceType) {
        "Alérgico" -> MaterialTheme.colorScheme.error
        "Disgusta" -> MaterialTheme.colorScheme.secondary
        else -> Color(0xFF4CAF50) // Verde éxito
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (preference.preferenceType == "Alérgico") {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = food.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = " (${preference.preferenceType})",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
        Text(
            text = "Eliminar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.clickable { onRemove(preference) }
        )
    }
}