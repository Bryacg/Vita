package com.example.vita.ui.screens.Game

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vita.ui.components.GameCard
import com.example.vita.ui.components.game.GameHeroHeader
import java.io.File

@Composable
fun GameScreen(viewModel: GameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ── Receptor de Broadcast — escucha el resultado que envía Godot ──────
    // Godot hace: activity.sendBroadcast(Intent("com.example.vita.GAME_RESULT")
    //             .putExtra("game_result", "GANASTE"/"PERDISTE"))
    // y luego activity.finish(). Este receiver captura ese mensaje incluso
    // si la Activity de Godot ya se cerró antes de que termine el "launch".
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val resultado = intent?.getStringExtra("game_result")
                android.util.Log.d("GameLauncher", "📡 Broadcast recibido: '$resultado'")
                viewModel.onRegresarDeJuego(resultado)
            }
        }

        val filter = IntentFilter("com.example.vita.GAME_RESULT")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // El juego Godot es otra app (otro UID), así que el receiver
            // debe estar EXPORTADO para poder recibir su broadcast.
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // ── Launcher con DEBUG y reintentos (fallback por archivo) ────────────
    val juegoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // El broadcast normalmente ya habrá actualizado el estado para este punto,
        // ya que Godot envía el broadcast ANTES de finish(). Este bloque queda
        // como fallback únicamente si el broadcast no llegó (por ejemplo, en
        // versiones de Godot que aún escriben a archivo).
        if (!uiState.juegoActivo) {
            // El broadcast ya procesó el resultado → no hacer nada más.
            return@rememberLauncherForActivityResult
        }

        val archivo = File(context.getExternalFilesDir(null), "game_result.txt")
        android.util.Log.d("GameLauncher", "🎮 Intent regresó - Buscando archivo en: ${archivo.absolutePath}")

        val resultado = if (archivo.exists()) {
            try {
                val texto = archivo.readText().trim()
                android.util.Log.d("GameLauncher", "✅ Archivo encontrado. Contenido: '$texto'")
                archivo.delete()
                texto.ifBlank { null }
            } catch (e: Exception) {
                android.util.Log.e("GameLauncher", "❌ Error leyendo archivo: ${e.message}", e)
                null
            }
        } else {
            android.util.Log.w("GameLauncher", "⚠️ Archivo NO encontrado, ni llegó broadcast.")
            null
        }
        viewModel.onRegresarDeJuego(resultado)
    }

    LaunchedEffect(Unit) {
        viewModel.navegarAJuego.collect { packageName ->
            android.util.Log.d("GameLauncher", "🎮 Intentando lanzar: $packageName")
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                android.util.Log.d("GameLauncher", "✅ Intent creado, lanzando...")
                juegoLauncher.launch(intent)
            } else {
                android.util.Log.e("GameLauncher", "❌ APK no encontrada: $packageName")
                viewModel.onRegresarDeJuego(null)
            }
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