package com.example.vita.ui.components.retos

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vita.domain.model.Challenger

/**
 * Cabecera hero para RetosScreen.
 * Mismo patrón visual que GameHeroHeader y HomeHeroHeader,
 * adaptada al contexto de retos: muestra totales activos,
 * completados y en progreso del día.
 *
 * @param retos       Lista completa de retos cargados (para calcular stats).
 * @param filtroActual Filtro seleccionado actualmente ("DIARIO" o "SEMANAL").
 */
@Composable
fun RetosHeroHeader(
    retos: List<Challenger>,
    filtroActual: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "retosPulse")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.55f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 14f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    // Stats calculados desde la lista recibida (sin tocar el ViewModel)
    val retosFiltrados  = retos.filter { it.type.equals(filtroActual, ignoreCase = true) }
    val totalRetos      = retosFiltrados.size
    val completados     = retosFiltrados.count { it.status == "COMPLETED" }
    val enProgreso      = retosFiltrados.count { it.status == "PROGRESSO" }
    val activos         = retosFiltrados.count {
        it.status == "ACTIVO" || it.status == "ACTIVE"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.80f),
                    )
                )
            )
    ) {
        // ── Decoración Canvas ─────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Círculo grande — esquina superior derecha
            drawCircle(
                color  = Color.White.copy(alpha = pulseAlpha * 0.06f),
                radius = 180.dp.toPx(),
                center = Offset(size.width * 0.90f, size.height * 0.10f)
            )
            // Círculo pequeño — esquina inferior izquierda
            drawCircle(
                color  = Color.White.copy(alpha = 0.04f),
                radius = 85.dp.toPx(),
                center = Offset(size.width * 0.06f, size.height * 1.05f)
            )
            // Arco dorado animado (flotante)
            drawCircle(
                color  = Color(0xFFFFC107).copy(alpha = pulseAlpha * 0.16f),
                radius = 50.dp.toPx(),
                center = Offset(
                    x = size.width * 0.80f,
                    y = size.height * 0.70f + floatOffset
                ),
                style = Stroke(
                    width       = 2.dp.toPx(),
                    cap         = StrokeCap.Round,
                    pathEffect  = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                )
            )
            // Punto accent verde claro
            drawCircle(
                color  = Color(0xFFCDEDA3).copy(alpha = 0.15f),
                radius = 30.dp.toPx(),
                center = Offset(size.width * 0.52f, size.height * 0.08f)
            )
        }

        // ── Contenido principal ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Título + ícono
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Flag,
                    contentDescription = null,
                    tint               = Color(0xFFFFC107),
                    modifier           = Modifier.size(30.dp)
                )
                Text(
                    text       = "Centro de Desafíos",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text     = if (filtroActual == "SEMANAL")
                    "Desafíos para toda la semana"
                else
                    "Completa tus retos de hoy",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── Píldoras de estadísticas ──────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                if (totalRetos > 0) {
                    RetoStatPill(
                        icon  = Icons.Default.TrendingUp,
                        texto = "$totalRetos retos",
                        color = Color(0xFFB1D18A)
                    )
                }
                if (completados > 0) {
                    RetoStatPill(
                        icon  = Icons.Default.CheckCircle,
                        texto = "$completados completados",
                        color = Color(0xFF69F0AE)
                    )
                }
                if (enProgreso > 0) {
                    RetoStatPill(
                        icon  = Icons.Default.LocalFireDepartment,
                        texto = "$enProgreso en progreso",
                        color = Color(0xFFFF7043)
                    )
                }
                // Fallback cuando no hay retos cargados aún
                if (totalRetos == 0) {
                    RetoStatPill(
                        icon  = Icons.Default.Flag,
                        texto = "Cargando retos...",
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

// ── Píldora de stat — mismo patrón que XpPill / HomeStatPill ─────────────────

@Composable
private fun RetoStatPill(
    icon  : ImageVector,
    texto : String,
    color : Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(13.dp)
        )
        Text(
            text       = texto,
            fontSize   = 11.sp,
            color      = Color.White.copy(alpha = 0.90f),
            fontWeight = FontWeight.Medium
        )
    }
}