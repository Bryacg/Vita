package com.example.vita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vita.R

@Composable
fun CardGame(titulo: String, descripcion: String, onclic: (() -> Unit)? = null) {
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text  = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = descripcion)
                }
                Image(
                    painter            = painterResource(id = R.drawable.logovt),
                    contentDescription = "Imagen del Juego",
                    modifier           = Modifier.size(85.dp).weight(1f)
                )
            }
            // Corregido: onClick ahora invoca el parámetro onclic en lugar de {}
            if (onclic != null) {
                Button(
                    onClick  = onclic,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Juego")
                }
            }
        }
    }
}