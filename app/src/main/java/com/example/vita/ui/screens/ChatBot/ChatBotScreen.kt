package com.example.vita.ui.screens.ChatBot

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.chatbot.BurbujaChat

// ✅ ChatUiState eliminado de aquí — vive en ChatBotViewModel.kt

@Composable
fun ChatBotFab(viewModel: ChatBotViewModel = hiltViewModel()) {
    val uiState: ChatUiState by viewModel.uiState.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    var userQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandIn(expandFrom = Alignment.BottomEnd),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.BottomEnd)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 16.dp)
                    .width(320.dp)
                    .height(450.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Chef IA - Recetas 🥗",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.messages) { msg ->
                            BurbujaChat(msg)
                        }
                        if (uiState.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterStart),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userQuery,
                            onValueChange = { userQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("¿Qué cocinamos hoy?") },
                            shape = RoundedCornerShape(16.dp),
                            enabled = !uiState.isLoading
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewModel.enviarMensaje(userQuery)
                                userQuery = ""
                            },
                            enabled = userQuery.isNotBlank() && !uiState.isLoading,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.padding(16.dp),
            containerColor = if (isExpanded)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.AutoAwesome,
                contentDescription = null
            )
        }
    }
}