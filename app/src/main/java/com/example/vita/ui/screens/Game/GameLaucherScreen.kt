package com.example.vita.ui.screens.Game

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File

@Composable
fun GameScreen(viewModel: GameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ── Launcher (lógica intacta) ─────────────────────────────────────────
    val juegoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val archivo = File(context.getExternalFilesDir(null), "game_result.txt")
        val resultado = if (archivo.exists()) {
            val texto = archivo.readText().trim()
            archivo.delete()
            texto.ifBlank { null }
        } else null
        viewModel.onRegresarDeJuego(resultado)
    }

    LaunchedEffect(Unit) {
        viewModel.navegarAJuego.collect { packageName ->
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) juegoLauncher.launch(intent)
            else viewModel.onRegresarDeJuego(null)
        }
    }

    // ── UI rediseñada ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera hero
        GameHeroHeader()

        // Mensaje de resultado (aparece cuando hay feedback)
        AnimatedVisibility(
            visible = uiState.mensajeResultado != null,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400, easing = EaseOutCubic)) { -40 }
        ) {
            uiState.mensajeResultado?.let { msg ->
                ResultBanner(
                    mensaje = msg,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }

        // Spinner si juego está activo
        if (uiState.juegoActivo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF4C662B)
                    )
                    Text(
                        text = "Abriendo juego...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sección de juegos
        Text(
            text = "Elige tu misión",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        GameCard(
            titulo = "Atrapa Saludable",
            descripcion = "Atrapa los alimentos saludables y esquiva la comida chatarra. ¡Cada captura suma XP!",
            xpRecompensa = 170,
            dificultad = "Normal",
            icono = Icons.Default.Restaurant,
            colorAcento = Color(0xFF1D9E75),
            colorFondo = Color(0xFFE1F5EE),
            enabled = !uiState.juegoActivo,
            onClick = { viewModel.solicitarAbrirJuego("com.example.atrapasalud") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        GameCard(
            titulo = "Nutri Defensores",
            descripcion = "Golpea la comida chatarra antes de que llegue. ¡Cuidado con las opciones saludables!",
            xpRecompensa = 170,
            dificultad = "Difícil",
            icono = Icons.Default.Bolt,
            colorAcento = Color(0xFFBA7517),
            colorFondo = Color(0xFFFAEEDA),
            enabled = !uiState.juegoActivo,
            onClick = { viewModel.solicitarAbrirJuego("com.example.velocidad") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Error silencioso
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// ── Cabecera hero ─────────────────────────────────────────────────────────────

@Composable
private fun GameHeroHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)

                    )
                )
            )
    ) {
        // Decoración de fondo: círculos sutiles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = pulseAlpha * 0.08f),
                radius = 160.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 80.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.9f)
            )
            drawCircle(
                color = Color(0xFFFFC107).copy(alpha = pulseAlpha * 0.12f),
                radius = 50.dp.toPx(),
                center = Offset(size.width * 0.75f, size.height * 0.75f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "VitaGames",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Juega, aprende y gana XP real",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Píldora de info
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XpPill(icon = Icons.Default.LocalFireDepartment, texto = "+170 XP por victoria")
                XpPill(icon = Icons.Default.EmojiEvents, texto = "Desbloquea logros")
            }
        }
    }
}

@Composable
private fun XpPill(icon: ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = texto,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Banner de resultado ───────────────────────────────────────────────────────

@Composable
private fun ResultBanner(mensaje: String, modifier: Modifier = Modifier) {
    val esVictoria = mensaje.contains("XP")
    val colorFondo = if (esVictoria) Color(0xFFE1F5EE) else Color(0xFFF1EFE8)
    val colorTexto = if (esVictoria) Color(0xFF0F6E56) else Color(0xFF5F5E5A)
    val colorBorde = if (esVictoria) Color(0xFF1D9E75) else Color(0xFFB4B2A9)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo)
            .border(1.dp, colorBorde, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (esVictoria) Icons.Default.EmojiEvents else Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = colorTexto,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = mensaje,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}

// ── Card de juego ─────────────────────────────────────────────────────────────

@Composable
fun GameCard(
    titulo: String,
    descripcion: String,
    xpRecompensa: Int,
    dificultad: String,
    icono: ImageVector,
    colorAcento: Color,
    colorFondo: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = tween(120),
        label = "scale"
    )

    val colorBorde = colorAcento.copy(alpha = if (enabled) 0.35f else 0.15f)
    val colorIconoBg = colorFondo
    val colorIcono = if (enabled) colorAcento else colorAcento.copy(alpha = 0.4f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .border(1.dp, colorBorde, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (enabled) 3.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Fila superior: icono + título + dificultad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono con fondo de color
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colorIconoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    // Badge de dificultad
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorAcento.copy(alpha = if (enabled) 0.12f else 0.06f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dificultad,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (enabled) colorAcento else colorAcento.copy(alpha = 0.5f)
                        )
                    }
                }

                // XP recompensa
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+$xpRecompensa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Color(0xFFBA7517) else Color(0xFFBA7517).copy(0.4f)
                    )
                    Text(
                        text = "XP",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Descripción
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Botón jugar
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorAcento,
                    contentColor = Color.White,
                    disabledContainerColor = colorAcento.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (enabled) "Jugar ahora" else "Juego activo...",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}