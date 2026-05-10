package com.example.vita.ui.screens.Progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import com.example.vita.ui.charts.WeeklyXpCaloriesChart
import com.example.vita.ui.components.CardStatus
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProgressScreen() {

    // 1. Crea el producer usando el patrón estándar de Vico 3.x
    val weeklyModelProducer = remember { CartesianChartModelProducer() }

    // 2. Datos de ejemplo: XP por día de la semana (Lun - Dom)
    val weeklyXpData = listOf(100f, 250f, 180f, 400f, 320f, 500f, 750f)

    // 3. Carga los datos en el producer al iniciar
    LaunchedEffect(weeklyXpData) {
        withContext(Dispatchers.Default) {
            weeklyModelProducer.runTransaction {
                lineSeries {
                    series(weeklyXpData)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Encabezado
        Text(
            text = "Tu Progreso en VitaGame",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Sigue mejorando tus hábitos para subir de nivel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            SmallStatCard(value = "2", label = "Nivel Alcanzado", modifier = Modifier.weight(1f))
            SmallStatCard(value = "2500", label = "XP Obtenida", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            SmallStatCard(value = "25", label = "Comidas Registradas", modifier = Modifier.weight(1f))
            SmallStatCard(value = "10", label = "Logros Obtenidos", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        CardStatus(
            nivel = 2,
            xpTotal = 2500,
            rachaActual = 5,
            rachaMaxima = 12,
            partidasJugadas = 8,
            totalComidas = 25,
            comidasBuenas = 15,
            comidasRegulares = 7,
            comidasMalas = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("Índice de Masa Corporal (IMC)")

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Progreso Diario")

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Sección Progreso Semanal con la gráfica
        SectionTitle("Progreso Semanal")

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "XP ganada esta semana",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
               /* WeeklyXpCaloriesChart(
                    modelProducer = weeklyModelProducer,
                    modifier = Modifier.fillMaxWidth()
                )*/
                // Etiquetas de días debajo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SmallStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}