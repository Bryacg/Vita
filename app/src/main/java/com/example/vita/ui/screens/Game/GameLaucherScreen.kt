package com.example.vita.ui.screens.Game

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vita.data.remote.godot.GameResultBuffer
import com.example.vita.ui.components.CardGame

@Composable
fun GameScreen(viewModel: GameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val juegoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Al regresar del juego leemos el buffer y lo limpiamos
        val resultado = GameResultBuffer.ultimoResultado
        GameResultBuffer.ultimoResultado = null
        viewModel.onRegresarDeJuego(resultado)
    }

    LaunchedEffect(Unit) {
        viewModel.navegarAJuego.collect { packageName ->
            val intent = context.packageManager
                .getLaunchIntentForPackage(packageName)
            if (intent != null) {
                juegoLauncher.launch(intent)
            } else {
                viewModel.onRegresarDeJuego(null)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text       = "¡Bienvenido a VitaGame!",
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        CardGame(
            titulo      = "Minijuegos",
            descripcion = "Juega y aprende"
        )

        Text(
            text  = "¡Los minijuegos se abrirán en pantalla completa!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CardGame(
            titulo      = "Atrapa Saludable",
            descripcion = "Atrapa comida saludable y evita la chatarra",
            onclic      = { viewModel.solicitarAbrirJuego("com.example.atrapasalud") }
        )

        CardGame(
            titulo      = "Velocidad",
            descripcion = "Presiona el botón lo más rápido posible",
            onclic      = { viewModel.solicitarAbrirJuego("com.example.velocidad") }
        )

        uiState.mensajeResultado?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text       = msg,
                    modifier   = Modifier.padding(16.dp),
                    style      = MaterialTheme.typography.titleMedium,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        uiState.error?.let { error ->
            Text(
                text  = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (uiState.juegoActivo) {
            CircularProgressIndicator()
        }
    }
}