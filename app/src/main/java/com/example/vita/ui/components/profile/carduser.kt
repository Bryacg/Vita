package com.example.vita.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User

@Composable
fun CardUser(user: User?, profile: Profile?, rachaActual: Int = 0) {
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text  = if (user != null) "${user.name} ${user.lastName}" else "Cargando...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = user?.email ?: "Sin correo registrado",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Edad: ${profile?.age ?: "--"} | ",  style = MaterialTheme.typography.bodySmall)
                Text("Peso: ${profile?.weight ?: "--"} kg | ", style = MaterialTheme.typography.bodySmall)
                Text("Altura: ${profile?.height ?: "--"} cm",  style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProfileStatColumn("${user?.currentLevel ?: 1}", "Nivel")
                ProfileStatColumn("${user?.currentXp ?: 0}", "Exp.")
                // Corregido: usa rachaActual del ViewModel, no "1" literal
                ProfileStatColumn("$rachaActual", "Racha")
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