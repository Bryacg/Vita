package com.example.vita.ui.components.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Meal

@Composable
fun CardComidasHoy(
    comidas: List<Meal>,
    onEliminar: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (comidas.isEmpty()) return

    var expandida by remember { mutableStateOf(true) }

    ElevatedCard(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Cabecera con toggle
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Lo que comiste hoy",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { expandida = !expandida }) {
                    Icon(
                        imageVector = if (expandida) Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = if (expandida) "Contraer" else "Expandir"
                    )
                }
            }

            if (expandida) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                comidas.forEach { comida ->
                    FilaComida(
                        comida    = comida,
                        onEliminar = { onEliminar(comida.id) }
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 2.dp),
                        thickness = 0.5.dp,
                        color     = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Total al pie
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Total del día",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = "${comidas.sumOf { it.calories }} kcal",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaComida(
    comida: Meal,
    onEliminar: () -> Unit
) {
    val scoreColor = when {
        comida.healthyScore >= 70 -> Color(0xFF2E7D32)
        comida.healthyScore >= 40 -> Color(0xFFF2994A)
        else                      -> Color(0xFFC62828)
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador de color de calidad
        Surface(
            modifier = Modifier.size(10.dp),
            shape    = MaterialTheme.shapes.small,
            color    = scoreColor
        ) {}

        Spacer(modifier = Modifier.width(8.dp))

        // Nombre de la comida
        Text(
            text     = comida.name,
            style    = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        // Calorías
        Text(
            text  = "${comida.calories} kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Puntuación de salud
        Text(
            text       = "${comida.healthyScore} pts",
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color      = scoreColor,
            modifier   = Modifier.width(52.dp)
        )

        // Botón eliminar
        IconButton(
            onClick  = onEliminar,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector       = Icons.Default.Delete,
                contentDescription = "Eliminar ${comida.name}",
                tint              = MaterialTheme.colorScheme.error,
                modifier          = Modifier.size(18.dp)
            )
        }
    }
}

