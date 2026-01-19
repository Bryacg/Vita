package com.example.vita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vita.R


@Composable
fun CardGame(titulo: String, descripcion: String,onclic: (()->Unit)? = null) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            // Usamos el Modifier para aplicar el borde manualmente
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Verde del tema con transparencia
                shape = CardDefaults.elevatedShape // Asegura que el borde siga la redondez de la tarjeta
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text =descripcion
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.logovt), // Reemplaza con tu ID
                    contentDescription = "Imagen del Juego",
                    modifier = Modifier
                        .size(85.dp)// Mantengo el tamaño, pero ahora está contenido en su tercio
                        .weight(1f)// Un pequeño espacio para que no pegue con el texto
                )
            }
            if(onclic!=null){
                Button(
                    onClick = { /* Acción */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Juego")
                }
            }

        }
    }
}