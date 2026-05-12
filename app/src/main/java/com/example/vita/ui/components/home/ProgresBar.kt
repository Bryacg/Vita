package com.example.vita.ui.components.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LineaBar(progresoActual: Int, progresoTotal: Int) {
    val factorDeProgreso = if (progresoTotal > 0) {
        progresoActual.toFloat() / progresoTotal.toFloat()
    } else {
        0f
    }

    LinearProgressIndicator(
        progress = { factorDeProgreso },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = Color(0xFFFFC107),
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}