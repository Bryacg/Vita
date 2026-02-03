package com.example.vita.ui.components.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// En com.example.vita.ui.components.profile.CardUser.kt

@Composable
fun CardUser(user: com.example.vita.domain.model.User?, profile: com.example.vita.domain.model.Profile?) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = CardDefaults.elevatedShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            // Nombre y Apellido reales desde Room
            Text(
                text = if (user != null) "${user.name} ${user.lastName}" else "Cargando...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // Correo real desde Room
            Text(
                text = user?.email ?: "Sin correo registrado",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Datos biométricos desde la tabla Profile
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(text = "Edad: ${profile?.age ?: "--"} | ", style = MaterialTheme.typography.bodySmall)
                Text(text = "Peso: ${profile?.weight ?: "--"} kg | ", style = MaterialTheme.typography.bodySmall)
                Text(text = "Altura: ${profile?.height ?: "--"} cm", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas de gamificación
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ProfileStatColumn("${user?.currentLevel ?: 1}", "Nivel")
                ProfileStatColumn("${user?.currentXp ?: 0}", "Exp.")
                ProfileStatColumn("1", "Racha")
            }
        }
    }
}

@Composable
fun ProfileStatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}