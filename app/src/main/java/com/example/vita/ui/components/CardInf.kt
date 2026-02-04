package com.example.vita.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.LevelCalculator
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User

@Composable
fun CardInf(user: User, progress: Progress) {
    // Obtenemos la información de nivel basada en la XP actual del usuario
    val infoNivel = LevelCalculator.calculateLevel(user.currentXp)

    // Cálculo de experiencia relativa para las barras de progreso
    val xpMinimaDelRango = infoNivel.range.first
    val xpMaximaDelRango = infoNivel.range.last
    val xpRelativaActual = user.currentXp - xpMinimaDelRango
    val xpRelativaTotal = xpMaximaDelRango - xpMinimaDelRango

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Información Personal y Título
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "${user.name} ${user.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = infoNivel.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Nivel ${user.currentLevel}")
                }
                // Progreso Circular basado en la experiencia relativa del nivel actual
                CircularProgreso(xpRelativaActual, xpRelativaTotal)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progreso Lineal y Detalles de XP
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progreso de Nivel")
                Text("$xpRelativaActual / $xpRelativaTotal XP")
            }
            LineaBar(xpRelativaActual, xpRelativaTotal)

            // Información adicional de progreso (ej. Racha)
            Text(
                text = "Racha actual: ${progress.streakDays} días",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}