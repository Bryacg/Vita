package com.example.vita.ui.screens.Progress

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
            ) {
        Text(text = "¡Bienvenido a VitaGame!")
        Text(text = "¡Esto es sera todo el progreso!")
        Row(){
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp).weight(1f)
                    // Usamos el Modifier para aplicar el borde manualmente
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                        shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
                    ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp
                )
            ){
                Column() {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "2"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "Nivel Alcanzado"
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp).fillMaxWidth().weight(1f)
                    // Usamos el Modifier para aplicar el borde manualmente
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                        shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
                    ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp
                )
            ){
                Column() {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "2500"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "Experiencia Obtenida"
                    )
                }
            }
        }
        Row(){
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp).weight(1f)
                    // Usamos el Modifier para aplicar el borde manualmente
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                        shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
                    ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp
                )
            ){
                Column() {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "25"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "Comidas Registradas"
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp).fillMaxWidth().weight(1f)
                    // Usamos el Modifier para aplicar el borde manualmente
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                        shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
                    ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp
                )
            ){
                Column() {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "10"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "Logros Obtenidos"
                    )
                }
            }
        }
        Text("ICM")
        Text("Tabla de Progreso Diario")
        Text("tabla de progreso semanal")

    }
}