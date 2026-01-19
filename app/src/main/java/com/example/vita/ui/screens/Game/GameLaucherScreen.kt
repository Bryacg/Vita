package com.example.vita.ui.screens.Game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vita.ui.components.CardGame

@Composable
fun GameScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(modifier = Modifier.padding(16.dp),
            text = "¡Bienvenido a VitaGame!")
        CardGame("Minijuegos","Juega y Aprende")
        Text(text = "¡Los Minijuegos se Abriran en Pantalla Completas!")
        CardGame("Atrapa Saludable","Atrapa comida Saludable y evita la comida chatarra",{})
        CardGame("Velocidad","Presiona el boton lo mas rapido posible",{})
    }
}