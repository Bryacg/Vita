package com.example.vita.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val IMC_MIN = 10f
private val IMC_MAX = 40f

private fun categoriaImc(imc: Float) = when {
    imc <= 0f -> Triple("Sin datos biométricos", Color.Gray, "--")
    imc < 18.5f -> Triple("Bajo peso", Color(0xFF2196F3), "%.1f".format(imc))
    imc < 25f -> Triple("Peso normal", Color(0xFF4CAF50), "%.1f".format(imc))
    imc < 30f -> Triple("Sobrepeso", Color(0xFFFFC107), "%.1f".format(imc))
    else -> Triple("Obesidad", Color(0xFFF44336), "%.1f".format(imc))
}

@Composable
fun BmiIndicator(imc: Float, modifier: Modifier = Modifier) {
    val (label, color, valorTexto) = categoriaImc(imc)

    val segmentos = listOf(
        Color(0xFF2196F3), // Bajo peso  10–18.5
        Color(0xFF4CAF50), // Normal     18.5–25
        Color(0xFFFFC107), // Sobrepeso  25–30
        Color(0xFFF44336)  // Obesidad   30–40
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Valor numérico
        Text(
            text = valorTexto,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Barra de colores con marcador
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            val segW = size.width / segmentos.size.toFloat()

            // Dibuja los segmentos de color
            segmentos.forEachIndexed { i, segColor ->
                val rx = if (i == 0) 7.dp.toPx() else 0f
                val rxRight = if (i == segmentos.lastIndex) 7.dp.toPx() else 0f

                drawRect(
                    color = segColor,
                    topLeft = Offset(i * segW, 0f),
                    size = Size(segW, size.height)
                )
            }

            // Marcador blanco + borde oscuro sobre el IMC del usuario
            if (imc > 0f) {
                val imcClamped = imc.coerceIn(IMC_MIN, IMC_MAX)
                val posX = ((imcClamped - IMC_MIN) / (IMC_MAX - IMC_MIN)) * size.width

                drawCircle(
                    color = Color.White,
                    radius = 9.dp.toPx(),
                    center = Offset(posX, size.height / 2)
                )
                drawCircle(
                    color = Color(0xFF333333),
                    radius = 5.dp.toPx(),
                    center = Offset(posX, size.height / 2)
                )
            }
        }

        // Etiquetas de referencia
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("10", "18.5", "25", "30", "40+").forEach { etiqueta ->
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Leyenda de categorías
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categorias = listOf(
                "Bajo peso" to Color(0xFF2196F3),
                "Normal" to Color(0xFF4CAF50),
                "Sobrepeso" to Color(0xFFFFC107),
                "Obesidad" to Color(0xFFF44336)
            )
            categorias.forEach { (nombre, cat) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = cat)
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}