package com.example.vita.ui.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Achievement

@Composable
fun DailyAchievementsChart(
    logros: List<Achievement>,
    modifier: Modifier = Modifier
) {
    val desbloqueados = logros.count { it.unlocked }
    val total         = logros.size

    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier             = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment    = Alignment.CenterVertically
        ) {
            Text(
                text       = "Logros desbloqueados",
                style      = MaterialTheme.typography.labelMedium,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text       = "$desbloqueados / $total",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFFDAA520)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fila de burbujas: dorada = desbloqueado, gris = bloqueado
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            logros.forEach { logro ->
                val color = if (logro.unlocked) Color(0xFFDAA520)
                else MaterialTheme.colorScheme.surfaceVariant

                Surface(
                    modifier = Modifier.size(36.dp),
                    shape    = CircleShape,
                    color    = color,
                    tonalElevation = if (logro.unlocked) 4.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text  = if (logro.unlocked) "★" else "○",
                            color = if (logro.unlocked) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (logros.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = if (desbloqueados == total) "Todos los logros obtenidos"
                else "${total - desbloqueados} logros pendientes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}