package com.example.vita.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LineaBar(progresoActual:Int,progresoTotal:Int){
    // 1. Calculamos el factor (de 0.0 a 1.0)
    // Es CRÍTICO usar .toFloat() para evitar que la división de 0
    val factorDeProgreso = if (progresoTotal > 0) {
        progresoActual.toFloat() / progresoTotal.toFloat()
    } else {
        0f // Evitamos error de división por cero
    }
    LinearProgressIndicator(
        progress = factorDeProgreso,
        modifier = Modifier.fillMaxWidth().height(8.dp),
        color = Color(0xFFFFC107), // Color Naranja/Amarillo de la barra
        trackColor = MaterialTheme.colorScheme.surfaceVariant // Fondo gris de la barra
    )
}