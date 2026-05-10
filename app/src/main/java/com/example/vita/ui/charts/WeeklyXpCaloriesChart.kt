package com.example.vita.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Progress
import java.util.Calendar

@Composable
fun WeeklyXpChart(
    progresoDeSemana: List<Progress>,
    modifier: Modifier = Modifier
) {
    val colorBarra = Color(0xFFFFC107)
    val colorBarraVacia = MaterialTheme.colorScheme.surfaceVariant
    val colorTexto = MaterialTheme.colorScheme.onSurfaceVariant

    // Construye array de 7 días (hace 6 días → hoy)
    val diasTimestamp = (6 downTo 0).map { diasAtras ->
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -diasAtras)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val etiquetas = diasTimestamp.map { ts ->
        val cal = Calendar.getInstance().apply { timeInMillis = ts }
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lun"
            Calendar.TUESDAY -> "Mar"
            Calendar.WEDNESDAY -> "Mié"
            Calendar.THURSDAY -> "Jue"
            Calendar.FRIDAY -> "Vie"
            Calendar.SATURDAY -> "Sáb"
            else -> "Dom"
        }
    }

    // XP por día: busca en la lista, si no hay registro → 0
    val xpPorDia = diasTimestamp.map { dayStart ->
        progresoDeSemana.find { it.date == dayStart }?.xp ?: 0
    }

    val maxXp = xpPorDia.maxOrNull()?.takeIf { it > 0 } ?: 1

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val totalWidth = size.width
            val barSlot = totalWidth / 7f
            val barWidth = barSlot * 0.55f
            val maxHeight = size.height - 16.dp.toPx()

            xpPorDia.forEachIndexed { index, xp ->
                val barHeight = (xp.toFloat() / maxXp) * maxHeight
                val x = index * barSlot + (barSlot - barWidth) / 2f
                val y = size.height - barHeight

                drawRoundRect(
                    color = if (xp > 0) colorBarra else colorBarraVacia,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight.coerceAtLeast(6.dp.toPx())),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )

                // Valor encima de la barra (solo si tiene XP)
                if (xp > 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#FFC107")
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            if (xp >= 1000) "${xp / 1000}k" else xp.toString(),
                            x + barWidth / 2f,
                            (y - 6.dp.toPx()).coerceAtLeast(20f),
                            paint
                        )
                    }
                }
            }
        }

        // Etiquetas de días
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            etiquetas.forEachIndexed { index, label ->
                val esCero = xpPorDia[index] == 0
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (esCero) colorTexto.copy(alpha = 0.4f) else colorTexto
                )
            }
        }
    }
}