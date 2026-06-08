package com.example.vita.ui.components

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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
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

/**
 * Cabecera hero para ProgressScreen.
 * Mismo patrón visual que GameHeroHeader / HomeHeroHeader / RetosHeroHeader,
 * adaptada al contexto de progreso: muestra nivel, XP total, racha
 * y logros desbloqueados.
 *
 * @param nivel                 Nivel actual del usuario.
 * @param xpTotal               XP total acumulada.
 * @param rachaActual           Racha de días consecutivos.
 * @param logrosDesbloqueados   Cantidad de logros obtenidos.
 * @param totalLogros           Total de logros disponibles.
 */
@Composable
fun ProgresoHeroHeader(
    nivel               : Int,
    xpTotal             : Int,
    rachaActual         : Int,
    logrosDesbloqueados : Int,
    totalLogros         : Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progresoPulse")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.22f,
        targetValue   = 0.58f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 16f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    // Rotación sutil del arco decorativo
    val rotateArc by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(18000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotateArc"
    )

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
            // Círculo grande translúcido — esquina superior derecha
            drawCircle(
                color  = Color.White.copy(alpha = pulseAlpha * 0.06f),
                radius = 185.dp.toPx(),
                center = Offset(size.width * 0.92f, size.height * 0.08f)
            )
            // Círculo medio — esquina inferior izquierda
            drawCircle(
                color  = Color.White.copy(alpha = 0.035f),
                radius = 95.dp.toPx(),
                center = Offset(size.width * 0.05f, size.height * 1.10f)
            )
            // Arco dorado flotante con dash
            drawCircle(
                color  = Color(0xFFFFC107).copy(alpha = pulseAlpha * 0.18f),
                radius = 48.dp.toPx(),
                center = Offset(
                    x = size.width * 0.82f,
                    y = size.height * 0.68f + floatOffset
                ),
                style = Stroke(
                    width      = 2.dp.toPx(),
                    cap        = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f))
                )
            )
            // Segundo arco más pequeño — accent verde claro
            drawCircle(
                color  = Color(0xFFCDEDA3).copy(alpha = pulseAlpha * 0.12f),
                radius = 26.dp.toPx(),
                center = Offset(size.width * 0.60f, size.height * 0.07f),
                style  = Stroke(width = 1.5.dp.toPx())
            )
            // Punto sólido pequeño
            drawCircle(
                color  = Color(0xFFB1D18A).copy(alpha = 0.20f),
                radius = 14.dp.toPx(),
                center = Offset(size.width * 0.18f, size.height * 0.22f)
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
                    imageVector        = Icons.Default.BarChart,
                    contentDescription = null,
                    tint               = Color(0xFFFFC107),
                    modifier           = Modifier.size(30.dp)
                )
                Text(
                    text       = "Mi Progreso",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo contextual según la racha
            Text(
                text = when {
                    rachaActual >= 7  -> "¡Racha épica! Sigue imparable 🔥"
                    rachaActual >= 3  -> "¡Vas bien! Mantén la racha activa"
                    rachaActual > 0   -> "Primer paso dado, no lo detengas"
                    else              -> "Empieza hoy tu primera racha"
                },
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── Píldoras de estadísticas ──────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                ProgresoStatPill(
                    icon  = Icons.Default.Star,
                    texto = "Niv. $nivel",
                    color = Color(0xFFFFC107)
                )
                ProgresoStatPill(
                    icon  = Icons.Default.TrendingUp,
                    texto = "$xpTotal XP",
                    color = Color(0xFFB1D18A)
                )
                if (rachaActual > 0) {
                    ProgresoStatPill(
                        icon  = Icons.Default.LocalFireDepartment,
                        texto = "$rachaActual días",
                        color = Color(0xFFFF7043)
                    )
                }
                ProgresoStatPill(
                    icon  = Icons.Default.EmojiEvents,
                    texto = "$logrosDesbloqueados/$totalLogros logros",
                    color = Color(0xFFDAA520)
                )
            }
        }
    }
}

// ── Píldora de stat — mismo patrón que las demás Hero Headers ────────────────

@Composable
private fun ProgresoStatPill(
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