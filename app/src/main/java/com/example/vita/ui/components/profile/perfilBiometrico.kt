package com.example.vita.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun perfilBiometrico(
    profile: com.example.vita.domain.model.Profile?,
    // Nota: Cambié gender a String si es "M/F", o cámbialo a Int si usas 0/1
    onGuardar: (peso: Float, altura: Float, edad: Int, gender: String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    var editPeso by remember(profile) { mutableStateOf(profile?.weight?.toString() ?: "") }
    var editAltura by remember(profile) { mutableStateOf(profile?.height?.toString() ?: "") }
    var editEdad by remember(profile) { mutableStateOf(profile?.age?.toString() ?: "") }
    // El género suele ser String ("M", "F") o Int (0, 1). Ajustémoslo a String por el Label "M/F"
    var editGender by remember(profile) { mutableStateOf(profile?.gender ?: "M") }

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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editar Perfil Biométrico",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = if (isExpanded) "▲" else "▼")
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editAltura,
                        onValueChange = { editAltura = it },
                        label = { Text("Altura (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPeso,
                        onValueChange = { editPeso = it },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editEdad,
                        onValueChange = { editEdad = it },
                        label = { Text("Edad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Selector de Género Simple
                    OutlinedTextField(
                        value = editGender,
                        onValueChange = {
                            if (it.length <= 1) editGender = it.uppercase()
                        },
                        label = { Text("Género (M/F)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("M para Masculino, F para Femenino") }
                    )

                    Button(
                        onClick = {
                            val peso = editPeso.toFloatOrNull() ?: 0f
                            val altura = editAltura.toFloatOrNull() ?: 0f
                            val edad = editEdad.toIntOrNull() ?: 0
                            val gender = if (editGender.isNotBlank()) editGender else "M"

                            onGuardar(peso, altura, edad, gender)
                            isExpanded = false
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Actualizar Biometría")
                    }
                }
            }
        }
    }
}