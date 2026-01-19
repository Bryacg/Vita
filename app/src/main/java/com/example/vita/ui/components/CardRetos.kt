package com.example.vita.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardRetos(titulo: String, descripcion: String, progreso: String, numeroRetos: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            // Usamos el Modifier para aplicar el borde manualmente
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(descripcion)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progreso")
                Text("$progreso/$numeroRetos")
            }
            LineaBar(progreso.toInt(),numeroRetos.toInt())
            Button(
                onClick = { /* Acción */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Reto")
            }
        }
    }
}
