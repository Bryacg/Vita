package com.example.vita.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CardStatus(
    nivel: Int,
    xpTotal: Int,
    rachaActual: Int,
    rachaMaxima: Int,
    partidasJugadas: Int,
    totalComidas: Int,
    comidasBuenas: Int,
    comidasRegulares: Int,
    comidasMalas: Int
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estadísticas de VitaGame",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SECCIÓN PROGRESIÓN
            StatRow(label = "Nivel Actual", value = "$nivel")
            StatRow(label = "XP Total acumulada", value = "$xpTotal")
            StatRow(label = "Racha Actual", value = "🔥 $rachaActual días")
            StatRow(label = "Racha Máxima", value = "$rachaMaxima días")
            StatRow(label = "Partidas Jugadas", value = "$partidasJugadas")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            // SECCIÓN NUTRICIÓN
            StatRow(label = "Total de Comidas", value = "$totalComidas")
            StatRow(label = "Buenas", value = "$comidasBuenas", valueColor = Color(0xFF2E7D32))
            StatRow(label = "Regulares", value = "$comidasRegulares", valueColor = Color(0xFFF2994A))
            StatRow(label = "Malas", value = "$comidasMalas", valueColor = Color(0xFFC62828))
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
    }
}