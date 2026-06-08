package com.example.vita.ui.components.profile

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
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
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User

/**
 * Cabecera hero para ProfileScreen.
 * Mismo patrón visual que las demás Hero Headers,
 * adaptada al contexto del perfil: muestra nombre completo,
 * email, nivel, logros y si el perfil biométrico está completo.
 *
 * @param user      Modelo de dominio del usuario (puede ser null mientras carga).
 * @param profile   Perfil biométrico del usuario (puede ser null si no fue completado).
 * @param logrosDesbloqueados  Cantidad de logros obtenidos.
 * @param totalLogros          Total de logros disponibles.
 * @param rachaActual          Racha de días consecutivos.
 */
@Composable
fun PerfilHeroHeader(
    user                : User?,
    profile             : Profile?,
    logrosDesbloqueados : Int,
    totalLogros         : Int,
    rachaActual         : Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "perfilPulse")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.18f,
        targetValue   = 0.52f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 13f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 0.92f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Datos derivados sin tocar el ViewModel
    val nombreCompleto   = if (user != null) "${user.name} ${user.lastName}".trim() else "Cargando..."
    val emailUsuario     = user?.email ?: ""
    val nivel            = user?.currentLevel ?: 1
    val perfilCompleto   = profile != null && profile.weight > 0f

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
                radius = 175.dp.toPx(),
                center = Offset(size.width * 0.91f, size.height * 0.09f)
            )
            // Círculo medio — esquina inferior izquierda
            drawCircle(
                color  = Color.White.copy(alpha = 0.03f),
                radius = 100.dp.toPx(),
                center = Offset(size.width * 0.04f, size.height * 1.08f)
            )
            // Arco dorado flotante con dash
            drawCircle(
                color  = Color(0xFFFFC107).copy(alpha = pulseAlpha * 0.17f),
                radius = 46.dp.toPx() * pulseScale,
                center = Offset(
                    x = size.width * 0.81f,
                    y = size.height * 0.66f + floatOffset
                ),
                style = Stroke(
                    width      = 2.dp.toPx(),
                    cap        = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 6f))
                )
            )
            // Anillo accent verde claro — posición media-arriba
            drawCircle(
                color  = Color(0xFFCDEDA3).copy(alpha = pulseAlpha * 0.10f),
                radius = 32.dp.toPx(),
                center = Offset(size.width * 0.58f, size.height * 0.06f),
                style  = Stroke(width = 1.5.dp.toPx())
            )
            // Punto sólido pequeño — esquina superior izquierda
            drawCircle(
                color  = Color(0xFFB1D18A).copy(alpha = 0.18f),
                radius = 16.dp.toPx(),
                center = Offset(size.width * 0.15f, size.height * 0.18f)
            )
        }

        // ── Contenido principal ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Ícono + nombre
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint               = Color(0xFFFFC107),
                    modifier           = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text       = nombreCompleto,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        maxLines   = 1
                    )
                    if (emailUsuario.isNotBlank()) {
                        Text(
                            text     = emailUsuario,
                            fontSize = 11.sp,
                            color    = Color.White.copy(alpha = 0.60f),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtítulo contextual según estado del perfil
            Text(
                text = when {
                    !perfilCompleto -> "Completa tu perfil biométrico para personalizar tus retos"
                    rachaActual >= 7 -> "¡Perfil completo y racha épica activa! 🔥"
                    else -> "Gestiona tus datos, preferencias y recordatorios"
                },
                fontSize = 12.sp,
                color    = Color.White.copy(alpha = 0.70f),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Píldoras de estadísticas ──────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                PerfilStatPill(
                    icon  = Icons.Default.Star,
                    texto = "Nivel $nivel",
                    color = Color(0xFFFFC107)
                )
                PerfilStatPill(
                    icon  = Icons.Default.EmojiEvents,
                    texto = "$logrosDesbloqueados/$totalLogros logros",
                    color = Color(0xFFDAA520)
                )
                if (rachaActual > 0) {
                    PerfilStatPill(
                        icon  = Icons.Default.LocalFireDepartment,
                        texto = "$rachaActual días",
                        color = Color(0xFFFF7043)
                    )
                }
                PerfilStatPill(
                    icon  = Icons.Default.FitnessCenter,
                    texto = if (perfilCompleto) "${profile!!.weight}kg" else "Sin datos",
                    color = if (perfilCompleto) Color(0xFF69F0AE) else Color(0xFFBDBDBD)
                )
            }
        }
    }
}

// ── Píldora de stat — mismo patrón que las demás Hero Headers ────────────────

@Composable
private fun PerfilStatPill(
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