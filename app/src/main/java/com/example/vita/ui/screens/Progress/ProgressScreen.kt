package com.example.vita.ui.screens.Progress

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vita.ui.charts.BmiIndicator
import com.example.vita.ui.charts.WeeklyXpChart

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Cabecera ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mi progreso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refrescar() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
            }
        }

        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Grid de estadísticas 2×2 ──────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    titulo = "Nivel",
                    valor = "${uiState.nivelActual}",
                    subtitulo = "alcanzado",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Experiencia",
                    valor = "${uiState.xpTotal}",
                    subtitulo = "XP acumulada",
                    color = Color(0xFFFFC107),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    titulo = "Comidas",
                    valor = "${uiState.totalComidas}",
                    subtitulo = "registradas",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Logros",
                    valor = "${uiState.logrosDesbloqueados}/${uiState.totalLogros}",
                    subtitulo = "obtenidos",
                    color = Color(0xFFDAA520),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Racha ─────────────────────────────────────────────────
        RachaCard(
            racha = uiState.rachaActual,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── IMC ───────────────────────────────────────────────────
        SeccionCard(titulo = "Índice de masa corporal") {
            BmiIndicator(imc = uiState.imc)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Gráfico XP semanal ────────────────────────────────────
        SeccionCard(titulo = "XP ganada esta semana") {
            if (uiState.progresoDeSemana.isEmpty() && !uiState.isLoading) {
                Text(
                    text = "Completa retos o minijuegos para ver tu progreso aquí.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                WeeklyXpChart(progresoDeSemana = uiState.progresoDeSemana)
            }
        }

        // ── Error (opcional, no invasivo) ─────────────────────────
        uiState.error?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Algunos datos no pudieron cargarse.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // espacio para el BottomBar
    }
}

// ── Componentes internos ──────────────────────────────────────────

@Composable
private fun StatCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.4f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RachaCard(racha: Int, modifier: Modifier = Modifier) {
    val color = if (racha > 0) Color(0xFFE64A19) else MaterialTheme.colorScheme.onSurfaceVariant

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.4f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    text = if (racha > 0) "$racha días seguidos" else "Sin racha activa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = if (racha > 0) "¡Sigue así! No rompas la racha." else "Completa un reto hoy para empezar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SeccionCard(titulo: String, content: @Composable () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}