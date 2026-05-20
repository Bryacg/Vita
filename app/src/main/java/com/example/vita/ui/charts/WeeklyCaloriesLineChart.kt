package com.example.vita.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun WeeklyCaloriesLineChart(
    caloriasSemanales: List<Int>, // 7 valores — uno por día
    modifier: Modifier = Modifier
) {
    // ── Colores ────────────────────────────────────────────────────────────
    val lineColor    = Color(0xFF26A69A)             // teal (distinto del XP en amber)
    val dotColor     = Color(0xFF00796B)             // teal oscuro
    val gridColor    = Color.Gray.copy(alpha = 0.12f)
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val density      = LocalDensity.current          // para convertir sp → px en Canvas

    // ── Datos seguros (siempre 7 valores) ──────────────────────────────────
    val datos = List(7) { caloriasSemanales.getOrElse(it) { 0 } }

    // ── Etiquetas de días ──────────────────────────────────────────────────
    val etiquetas = (6 downTo 0).map { daysAgo ->
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY    -> "Lun"
            Calendar.TUESDAY   -> "Mar"
            Calendar.WEDNESDAY -> "Mié"
            Calendar.THURSDAY  -> "Jue"
            Calendar.FRIDAY    -> "Vie"
            Calendar.SATURDAY  -> "Sáb"
            else               -> "Dom"
        }
    }

    // ── Stats ──────────────────────────────────────────────────────────────
    val diasConDatos    = datos.count { it > 0 }
    val totalSemana     = datos.sum()
    val promedioDiario  = if (diasConDatos > 0) totalSemana / diasConDatos else 0
    val maxCal          = datos.maxOrNull()
        ?.takeIf { it > 0 }
        ?.let { ((it / 500) + 1) * 500 }   // redondea al siguiente múltiplo de 500
        ?: 2000

    Column(modifier = modifier.fillMaxWidth()) {

        // ── Fila de resumen ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ResumenChip(
                etiqueta = "Total semana",
                valor    = "$totalSemana kcal",
                color    = lineColor
            )
            ResumenChip(
                etiqueta = "Promedio diario",
                valor    = "$promedioDiario kcal",
                color    = dotColor
            )
        }

        // ── Canvas principal ───────────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
        ) {
            val padLeft   = 8.dp.toPx()
            val padRight  = 8.dp.toPx()
            val padTop    = 28.dp.toPx()  // espacio para etiquetas sobre los puntos
            val padBottom = 4.dp.toPx()

            val cLeft   = padLeft
            val cRight  = size.width - padRight
            val cTop    = padTop
            val cBottom = size.height - padBottom
            val cWidth  = cRight - cLeft
            val cHeight = cBottom - cTop
            val xStep   = cWidth / 6f

            // Coordenadas de los 7 puntos
            val puntos = datos.mapIndexed { i, kcal ->
                val x = cLeft + i * xStep
                val y = if (maxCal > 0) {
                    cBottom - (kcal.toFloat() / maxCal) * cHeight
                } else cBottom
                Offset(x, y.coerceIn(cTop, cBottom))
            }

            // ── Grid horizontal (5 líneas) ─────────────────────────────────
            repeat(5) { i ->
                val y = cTop + (cHeight / 4f) * i
                drawLine(
                    color       = gridColor,
                    start       = Offset(cLeft, y),
                    end         = Offset(cRight, y),
                    strokeWidth = 0.5.dp.toPx()
                )
            }

            // ── Construye ruta suave (Catmull-Rom → Bezier cúbico) ─────────
            fun buildSmoothPath(pts: List<Offset>): Path {
                val path = Path()
                if (pts.size < 2) return path
                path.moveTo(pts[0].x, pts[0].y)
                for (i in 0 until pts.size - 1) {
                    val p0 = if (i > 0) pts[i - 1] else pts[i]
                    val p1 = pts[i]
                    val p2 = pts[i + 1]
                    val p3 = if (i + 2 < pts.size) pts[i + 2] else pts[i + 1]
                    val cp1 = Offset(
                        x = p1.x + (p2.x - p0.x) / 6f,
                        y = p1.y + (p2.y - p0.y) / 6f
                    )
                    val cp2 = Offset(
                        x = p2.x - (p3.x - p1.x) / 6f,
                        y = p2.y - (p3.y - p1.y) / 6f
                    )
                    path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y)
                }
                return path
            }

            val linePath = buildSmoothPath(puntos)

            // ── Fill degradado bajo la línea ───────────────────────────────
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(puntos.last().x, cBottom)
                lineTo(puntos.first().x, cBottom)
                close()
            }
            drawPath(
                path  = fillPath,
                brush = Brush.verticalGradient(
                    colors   = listOf(
                        lineColor.copy(alpha = 0.30f),
                        lineColor.copy(alpha = 0.00f)
                    ),
                    startY = cTop,
                    endY   = cBottom
                )
            )

            // ── Línea suave ────────────────────────────────────────────────
            drawPath(
                path  = linePath,
                color = lineColor,
                style = Stroke(
                    width = 2.2.dp.toPx(),
                    cap   = StrokeCap.Round,
                    join  = StrokeJoin.Round
                )
            )

            // ── Puntos y etiquetas de valor ────────────────────────────────
            val labelSize = with(density) { 10.sp.toPx() }

            puntos.forEachIndexed { idx, punto ->
                val kcal = datos[idx]

                // Halo blanco
                drawCircle(
                    color  = Color.White,
                    radius = 5.5.dp.toPx(),
                    center = punto
                )
                // Punto principal
                drawCircle(
                    color  = if (kcal > 0) dotColor else Color.Gray.copy(alpha = 0.25f),
                    radius = 3.8.dp.toPx(),
                    center = punto
                )

                // Etiqueta de valor sobre el punto
                if (kcal > 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color         = android.graphics.Color.parseColor("#00796B")
                            textSize      = labelSize
                            textAlign     = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            isAntiAlias   = true
                        }
                        val texto = if (kcal >= 1000) "${"%.1f".format(kcal / 1000f)}k" else "$kcal"
                        drawText(
                            texto,
                            punto.x,
                            (punto.y - 9.dp.toPx()).coerceAtLeast(labelSize + 4f),
                            paint
                        )
                    }
                }
            }
        }

        // ── Etiquetas de días ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            etiquetas.forEachIndexed { idx, label ->
                val tieneDatos = datos[idx] > 0
                Text(
                    text       = label,
                    style      = MaterialTheme.typography.labelSmall,
                    fontWeight = if (tieneDatos) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (tieneDatos) textSecondary
                    else textSecondary.copy(alpha = 0.35f)
                )
            }
        }
    }
}

// ── Composable auxiliar ────────────────────────────────────────────────────

@Composable
private fun ResumenChip(etiqueta: String, valor: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = valor,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = color
        )
        Text(
            text  = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

