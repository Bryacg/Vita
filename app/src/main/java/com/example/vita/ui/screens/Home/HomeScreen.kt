package com.example.vita.ui.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.ui.components.CardGame
import com.example.vita.ui.components.CardInf
import com.example.vita.ui.screens.ChatBot.ChatBotFab


@Composable
fun HomeScreen() {
    // 1. Usamos Box como contenedor principal
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. El Scaffold con tu contenido normal
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // Cambiado a Top para mejor orden
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "¡Bienvenido a VitaGame!", fontWeight = FontWeight.Bold)

                CardInf(
                    Nombres = "Bryan Michael Choez Giler ",
                    Nivel = "Nivel: 5",
                    exp = 150,
                    expT = 300
                )

                CardGame(
                    titulo = "Velocidad",
                    descripcion = "Presiona el boton lo mas rapido posible",
                    onclic = {}
                )
            }
        }

        // 3. El ChatBotFab se pone FUERA del Scaffold o dentro de un Box superior
        // para que flote sobre todo lo anterior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Margen para el botón
            contentAlignment = Alignment.BottomEnd // Lo alinea abajo a la derecha
        ) {
            ChatBotFab()
        }
    }
}