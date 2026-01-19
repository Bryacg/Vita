package com.example.vita.ui.screens.Retos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vita.ui.components.CardRetos

@Composable
fun RetosScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier= Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                ,
            fontWeight = FontWeight.Bold,
            text = "¡Retos y Desafios!")
        Text(text = "Completa desafios para poder ganar puntos y subir de nivel")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Acción */ },
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            ) {
                Text("Retos Diarios")
            }
            Button(
                onClick = { /* Acción */ },
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            ) {
                Text("Reto Semanales")
            }
        }
        // Aquí irán tus retos generados por IA
        CardRetos("Vaso de Agua Diario","Bebe 8 Vasos de Agua Al Dia","3","7")
        CardRetos("Caminata","da 500 pasos diarios","60","500")
        CardRetos("Comida de Colores","ingiere una comida variqa de nutrientes como carne, vegetales, y frutas","6","7")
        CardRetos("Vaso de Agua Diario","Bebe 8 Vasos de Agua Al Dia","12","25")
    }
}