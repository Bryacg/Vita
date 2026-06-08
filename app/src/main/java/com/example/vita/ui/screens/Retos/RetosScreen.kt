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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.retos.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RetosScreen(viewModel: RetosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var filtroSeleccionado by remember { mutableStateOf("DIARIO") }

    LaunchedEffect(Unit) {
        viewModel.cargarRetos()
    }

    Column(
        modifier            = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── 1. Cabecera hero ──────────────────────────────────────────────
        // Recibe la lista de retos ya cargada y el filtro activo para
        // calcular las stats inline sin tocar el ViewModel.
        RetosHeroHeader(
            retos        = uiState.retos,
            filtroActual = filtroSeleccionado
        )

        // ── 2. Contenido scrollable debajo de la cabecera ─────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Spinner de carga con mensaje de IA
            if (uiState.isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    if (!uiState.mensajeCarga.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.mensajeCarga!!,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // ── Selector de tipo ──────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado == "DIARIO",
                    onClick  = { filtroSeleccionado = "DIARIO" },
                    label    = { Text("Diarios") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filtroSeleccionado == "SEMANAL",
                    onClick  = { filtroSeleccionado = "SEMANAL" },
                    label    = { Text("Semanales") },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Lista de retos filtrada ───────────────────────────────────
            val retosFiltrados = uiState.retos.filter {
                it.type.equals(filtroSeleccionado, ignoreCase = true)
            }

            if (retosFiltrados.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val mensaje = if (filtroSeleccionado == "SEMANAL")
                        "Los retos semanales se generan cada lunes\ny duran hasta el domingo."
                    else
                        "No tienes retos diarios para hoy."

                    Text(
                        text  = mensaje,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            retosFiltrados.forEach { reto ->
                key(reto.id, reto.currentValue, reto.status) {
                    CardRetosD(
                        challenger    = reto,
                        onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                        onLongClick   = { viewModel.completarRetoInstantaneo(reto) }
                    )
                }
            }

            // ── Error ─────────────────────────────────────────────────────
            uiState.error?.let { error ->
                Text(
                    text     = error,
                    color    = MaterialTheme.colorScheme.error,
                    style    = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}