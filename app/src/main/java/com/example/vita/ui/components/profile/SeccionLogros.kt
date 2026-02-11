package com.example.vita.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Achievement

@Composable
fun SeccionLogros(logros: List<Achievement>) {
    val desbloqueados = logros.count { it.unlocked }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título de la sección
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFDAA520)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Colección de Logros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "$desbloqueados de ${logros.size} medallas obtenidas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Renderizado de la lista
        logros.forEach { logro ->
            TarjetaLogro(logro = logro)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TarjetaLogro(logro: Achievement) {
    val colorBorde = if (logro.unlocked) Color(0xFFFFD700) else Color.Transparent
    val colorFondo = if (logro.unlocked) Color(0xFFFFFBE6) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val alphaContenido = if (logro.unlocked) 1f else 0.4f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorFondo,
        border = if (logro.unlocked) BorderStroke(2.dp, colorBorde) else null,
        tonalElevation = 2.dp,
        shadowElevation = if (logro.unlocked) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del logro
            IconoLogro(logro.name, logro.unlocked, alphaContenido)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = logro.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alphaContenido)
                    )
                    if (logro.unlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = logro.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alphaContenido)
                )
            }
        }
    }
}

@Composable
fun IconoLogro(nombre: String, unlocked: Boolean, alpha: Float) {
    val icon = when {
        nombre.contains("Comida", ignoreCase = true) -> Icons.Default.Restaurant
        nombre.contains("Racha", ignoreCase = true) -> Icons.Default.LocalFireDepartment
        nombre.contains("Nivel", ignoreCase = true) -> Icons.Default.Star
        else -> Icons.Default.MilitaryTech
    }

    val colorIcono = if (unlocked) {
        when {
            nombre.contains("Racha") -> Color(0xFFE64A19)
            nombre.contains("Nivel") -> Color(0xFF2196F3)
            else -> Color(0xFFFFA000)
        }
    } else {
        Color.Gray
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = colorIcono.copy(alpha = alpha),
        modifier = Modifier.size(36.dp)
    )
}