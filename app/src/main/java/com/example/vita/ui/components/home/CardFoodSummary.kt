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


@Composable
fun CardAddMealForm(
    onSave: (String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Registrar Alimento",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("¿Qué comiste?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = calories,
                onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                label = { Text("Calorías (kcal)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (foodName.isNotBlank() && calories.isNotBlank()) onSave(foodName, calories.toInt()) },
                    enabled = foodName.isNotBlank() && calories.isNotBlank()
                ) { Text("Guardar") }
            }
        }
    }
}

// COMPONENTE 2: El que te faltaba (RESUMEN)
@Composable
fun CardFoodSummary(
    totalCalories: Int,
    averageHealthScore: Int,
    onAddClick: () -> Unit
) {
    val healthColor = if (averageHealthScore >= 70) Color(0xFF2E7D32) else Color(0xFFC62828)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f), CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text("Nutrición Diaria", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Consumo: $totalCalories kcal", style = MaterialTheme.typography.bodyMedium)
                }
                Surface(color = healthColor.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("$averageHealthScore pts", Modifier.padding(8.dp), color = healthColor, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("REGISTRAR COMIDA")
            }
        }
    }
}