package com.example.vita.ui.components.chatbot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChatPopUpCard(onClose: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(0.85f) // Que ocupe el 85% del ancho
            .height(450.dp),      // Altura fija para que sea un pop-up
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera del Chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Asistente de Recetas 🍏", fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            // Aquí integrarías la lógica de OpenAI
            // Por ahora, un espacio para el contenido
            Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text("Hola Bryan, ¿qué ingredientes tienes hoy para tu receta?")
            }

            // Campo de texto simple (Placeholder)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                placeholder = { Text("Ej: Tengo atún y espinacas...") },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
