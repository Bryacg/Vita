package com.example.vita.ui.components.home

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Cabecera hero para HomeScreen.
 * Mismo patrón visual que GameHeroHeader pero adaptada al contexto de
 * salud/gamificación del usuario: muestra nombre, nivel, XP y racha.
 *
 * @param nombreUsuario  Nombre a mostrar en el saludo.
 * @param nivel          Nivel actual del usuario.
 * @param xpTotal        XP acumulada total.
 * @param rachaActual    Racha de días consecutivos activa.
 */
@Composable
fun HomeHeroHeader(
    nombreUsuario: String,
    nivel: Int,
    xpTotal: Int,
    rachaActual: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "homePulse")

    // Pulso suave para los círculos decorativos del fondo
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue  = 0.60f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Movimiento flotante del círculo accent
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
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
        // ── Decoración de fondo: Canvas con círculos y arcos ─────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Círculo grande translúcido — esquina superior derecha
            drawCircle(
                color  = Color.White.copy(alpha = pulseAlpha * 0.07f),
                radius = 170.dp.toPx(),
                center = Offset(size.width * 0.88f, size.height * 0.15f)
            )
            // Círculo medio — esquina inferior izquierda
            drawCircle(
                color  = Color.White.copy(alpha = 0.04f),
                radius = 90.dp.toPx(),
                center = Offset(size.width * 0.08f, size.height * 1.0f)
            )
            // Arco dorado animado (accent) — flota suavemente
            drawCircle(
                color  = Color(0xFFFFC107).copy(alpha = pulseAlpha * 0.14f),
                radius = 55.dp.toPx(),
                center = Offset(
                    x = size.width * 0.78f,
                    y = size.height * 0.72f + floatOffset
                ),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            // Punto accent pequeño — complementa la composición
            drawCircle(
                color  = Color(0xFFB1D18A).copy(alpha = 0.18f),
                radius = 28.dp.toPx(),
                center = Offset(size.width * 0.55f, size.height * 0.10f)
            )
        }

        // ── Contenido principal ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Saludo + ícono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint               = Color(0xFFFFC107),
                    modifier           = Modifier.size(28.dp)
                )
                Text(
                    text       = "¡Hola, $nombreUsuario!",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text     = "Sigue construyendo tus hábitos hoy",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── Píldoras de estadísticas ──────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                HomeStatPill(
                    icon  = Icons.Default.Star,
                    texto = "Nivel $nivel",
                    color = Color(0xFFFFC107)
                )
                HomeStatPill(
                    icon  = Icons.Default.EmojiEvents,
                    texto = "$xpTotal XP",
                    color = Color(0xFFB1D18A)
                )
                if (rachaActual > 0) {
                    HomeStatPill(
                        icon  = Icons.Default.LocalFireDepartment,
                        texto = "$rachaActual días",
                        color = Color(0xFFFF7043)
                    )
                }
            }
        }
    }
}

// ── Píldora de stat — mismo patrón que XpPill en GameScreen ──────────────────

@Composable
private fun HomeStatPill(
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