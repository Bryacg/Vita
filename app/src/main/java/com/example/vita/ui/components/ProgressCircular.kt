package com.example.vita.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgreso(progresoActual: Int, progresoTotal: Int) {
    // Calculamos el factor (de 0.0 a 1.0)
    val factorDeProgreso = if (progresoTotal > 0) {
        progresoActual.toFloat() / progresoTotal.toFloat()
    } else {
        0f
    }

    CircularProgressIndicator(
        // En Material 3, se recomienda usar la lambda para el progreso
        progress = { factorDeProgreso },
        modifier = Modifier
            .size(100.dp) // Tamaño del círculo
            .padding(8.dp),
        color = Color(0xFFFFC107), // Tu color naranja/amarillo
        strokeWidth = 8.dp, // Grosor de la línea
        trackColor = MaterialTheme.colorScheme.surfaceVariant, // Fondo de la pista
        strokeCap = StrokeCap.Round // Hace que los bordes del progreso sean redondeados
    )
}