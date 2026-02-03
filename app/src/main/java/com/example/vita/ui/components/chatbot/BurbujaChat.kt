package com.example.vita.ui.components.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.ChatMessage

@Composable
fun BurbujaChat(mensaje: ChatMessage) {
    // Identificamos si el mensaje es del usuario o de la IA
    val esUsuario = mensaje.sender == "user"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (esUsuario) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (esUsuario) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (esUsuario) 16.dp else 0.dp,
                bottomEnd = if (esUsuario) 0.dp else 16.dp
            ),
            tonalElevation = 2.dp
        ) {
            Text(
                text = mensaje.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (esUsuario) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}