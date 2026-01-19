package com.example.vita.ui.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.vita.ui.components.CardGame
import com.example.vita.ui.components.CardInf
import com.example.vita.ui.components.CardRetos

@Composable
fun HomeScreen() {
    // Asegúrate de usar Scaffold para manejar los espacios de la BottomBar
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        // Es CRITICO usar paddingValues aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues), // Esto evita que el contenido choque con la barra inferior
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "¡Bienvenido a VitaGame!")

            CardInf(
                "Bryan Michael Choez Giler ",
                "Nivel: 5",
                150,
                300)
            CardRetos(
                titulo = "Reto de Ejercicio",
                descripcion = "Completa 30 minutos de ejercicio diario durante una semana.",
                progreso = "3",
                numeroRetos = "7"
            )
            CardGame("Velocidad","Presiona el boton lo mas rapido posible",{})


        }
    }
}