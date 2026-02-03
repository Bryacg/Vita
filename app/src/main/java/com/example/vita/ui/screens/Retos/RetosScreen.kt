package com.example.vita.ui.screens.Retos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.retos.CardRetos

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
        Text(text = "¡Retos y Desafíos!", fontWeight = FontWeight.Bold)

        // Indicadores de Depuración (Útiles para confirmar el éxito)
        Text(text = "Total en Base de Datos: ${uiState.retos.size}", color = androidx.compose.ui.graphics.Color.Red)

        // 1. Mostrar carga si la IA está trabajando
        if (uiState.isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text("Gemini está creando tus retos...")
        }

        // Botones de filtro
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Button(
                onClick = { filtroSeleccionado = "DIARIO" },
                modifier = Modifier.weight(1f).padding(4.dp),
                enabled = filtroSeleccionado != "DIARIO"
            ) { Text("Diarios") }

            Button(
                onClick = { filtroSeleccionado = "SEMANAL" },
                modifier = Modifier.weight(1f).padding(4.dp),
                enabled = filtroSeleccionado != "SEMANAL"
            ) { Text("Semanales") }
        }

        // Listado dinámico con filtrado insensible a mayúsculas/minúsculas
        val retosFiltrados = uiState.retos.filter {
            it.type.equals(filtroSeleccionado, ignoreCase = true)
        }
        android.util.Log.d("VITA_LOG", "Lista total: ${uiState.retos.size}, Filtrados: ${retosFiltrados.size}")

        if (retosFiltrados.isEmpty() && !uiState.isLoading) {
            Text(
                text = "No hay retos $filtroSeleccionado disponibles.",
                modifier = Modifier.padding(16.dp),
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }

        retosFiltrados.forEach { reto ->
            CardRetos(
                titulo = reto.name,
                descripcion = reto.description,
                progreso = reto.currentValue.toString(),
                numeroRetos = reto.targetValue.toString()
            )
        }
    }
}