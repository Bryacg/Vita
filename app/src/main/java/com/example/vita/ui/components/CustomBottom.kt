package com.example.vita.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.onTertiaryLight
import com.example.compose.primaryContainerLight
import com.example.compose.tertiaryLight


@Composable
fun CardCompor(titulo:String,descripcion:String,progresoact:Int,progresotota:Int){
    val Titulo: String = titulo
    val Descripcion:String=descripcion
    val progresoActual: Int=progresoact
    val progresoTotal: Int=progresotota
     ElevatedCard(
         modifier = Modifier.fillMaxWidth().padding(16.dp),
         elevation = CardDefaults.cardElevation( defaultElevation = 5.dp ),
         shape = MaterialTheme.shapes.medium,
         colors = CardDefaults.elevatedCardColors(primaryContainerLight) //primaryContainerLight
     ) {
         Column(
             modifier = Modifier.fillMaxWidth().padding(16.dp),
         ) {
             //Titulo
             Text(
                 text = Titulo,
                 style = MaterialTheme.typography.bodyLarge,
                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                 fontWeight = FontWeight.Bold,
             )
             //Descripcion
             Text(text=Descripcion)
             Spacer(modifier = Modifier.height(12.dp))
             //Barra de Progreso
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.SpaceBetween // Alinea a los extremos
             ) {
                 Text(
                     text = "Progreso",
                     style = MaterialTheme.typography.labelLarge,
                     color = MaterialTheme.colorScheme.onSurface
                 )
                 Text(
                     text = "$progresoActual/$progresoTotal",
                     style = MaterialTheme.typography.labelLarge,
                     fontWeight = FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onSurface
                 )
             }
             LineaBar(progresoActual,progresoTotal)
             //Boton
             Botonr()
         }
     }
}
@Preview()
@Composable
fun previo(){
  CardCompor("correr SAno","camina 5km",1,5,)
}

@Composable
fun Botonr(){
    Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor =tertiaryLight, // Tu nuevo color de fondo
            contentColor = onTertiaryLight          // El color del texto
        ),
    ) {
        Text("Comenzar")
    }
}
@Composable
fun LineaBar(progresoActual:Int,progresoTotal:Int){
    // 1. Calculamos el factor (de 0.0 a 1.0)
    // Es CRÍTICO usar .toFloat() para evitar que la división de 0
    val factorDeProgreso = if (progresoTotal > 0) {
        progresoActual.toFloat() / progresoTotal.toFloat()
    } else {
        0f // Evitamos error de división por cero
    }
    LinearProgressIndicator(
        progress = factorDeProgreso,
        modifier = Modifier.fillMaxWidth().height(8.dp),
        color = Color(0xFFFFC107), // Color Naranja/Amarillo de la barra
        trackColor = MaterialTheme.colorScheme.surfaceVariant // Fondo gris de la barra
    )
}