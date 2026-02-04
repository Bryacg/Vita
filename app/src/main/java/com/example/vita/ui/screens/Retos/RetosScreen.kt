package com.example.vita.ui.screens.Retos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.retos.CardRetosD // Asegúrate de importar tu nueva Card

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RetosScreen(viewModel: RetosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var filtroSeleccionado by remember { mutableStateOf("DIARIO") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado principal
        Text(
            text = "Centro de Desafíos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Estado de carga con diseño mejorado
        if (uiState.isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Diseñando tus retos con IA...", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Selector de tipo de reto (Filtros)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtroSeleccionado == "DIARIO",
                onClick = { filtroSeleccionado = "DIARIO" },
                label = { Text("Diarios") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = filtroSeleccionado == "SEMANAL",
                onClick = { filtroSeleccionado = "SEMANAL" },
                label = { Text("Semanales") },
                modifier = Modifier.weight(1f)
            )
        }

        // Lógica de filtrado
        val retosFiltrados = uiState.retos.filter {
            it.type.equals(filtroSeleccionado, ignoreCase = true)
        }

        // Mensaje cuando no hay datos
        if (retosFiltrados.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes retos ${filtroSeleccionado.lowercase()}s activos.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Renderizado de tarjetas usando el objeto Challenger completo
        // Dentro de RetosScreen.kt
        retosFiltrados.forEach { reto ->
            // Usamos key para que Compose sepa que este elemento debe reaccionar a cambios
            key(reto.id, reto.currentValue, reto.status) {
                CardRetosD(
                    challenger = reto,
                    onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                    onLongClick = { viewModel.completarRetoInstantaneo(reto) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}