package com.example.vita.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeccionRecordatoriosHabitos(
    aguaActivo: Boolean,
    aguaHora: String,
    caminarActivo: Boolean,
    caminarHora: String,
    onCambioRecordatorio: (String, Boolean, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text       = "Configuración de misiones",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(bottom = 12.dp)
        )

        ItemRecordatorio(
            titulo       = "Hidratación constante",
            descripcion  = "Beber agua aumenta tu energía (XP)",
            icono        = Icons.Default.WaterDrop,
            colorBase    = Color(0xFF2196F3),
            horaExterna  = aguaHora,
            activoExterno = aguaActivo,
            tipo         = "agua",
            onCambio     = onCambioRecordatorio
        )

        Spacer(modifier = Modifier.height(12.dp))

        ItemRecordatorio(
            titulo       = "Exploración diaria",
            descripcion  = "Caminar desbloquea nuevos logros",
            icono        = Icons.Default.DirectionsWalk,
            colorBase    = Color(0xFF4CAF50),
            horaExterna  = caminarHora,
            activoExterno = caminarActivo,
            tipo         = "caminar",
            onCambio     = onCambioRecordatorio
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemRecordatorio(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorBase: Color,
    horaExterna: String,
    activoExterno: Boolean,
    tipo: String,
    onCambio: (String, Boolean, String) -> Unit
) {
    // Corregido: usamos directamente activoExterno y horaExterna sin estado local
    // que se desincronizaría. El estado vive en el ViewModel.
    var mostrarReloj by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour   = horaExterna.split(":").getOrNull(0)?.toIntOrNull() ?: 8,
        initialMinute = horaExterna.split(":").getOrNull(1)?.toIntOrNull() ?: 0,
        is24Hour      = true
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (activoExterno)
                colorBase.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier        = Modifier
                        .size(48.dp)
                        .background(
                            if (activoExterno) colorBase else Color.Gray.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = titulo,      style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(text = descripcion, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Corregido: checked usa activoExterno directamente
                Switch(
                    checked         = activoExterno,
                    onCheckedChange = { nuevoEstado -> onCambio(tipo, nuevoEstado, horaExterna) }
                )
            }

            if (activoExterno) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = colorBase.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier     = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null,
                            modifier = Modifier.size(18.dp), tint = colorBase)
                        Spacer(modifier = Modifier.width(8.dp))
                        // Corregido: muestra horaExterna directamente
                        Text("Recordar a las: $horaExterna",
                            style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = { mostrarReloj = true },
                        colors  = ButtonDefaults.buttonColors(containerColor = colorBase),
                        shape   = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Configurar", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton    = {
                TextButton(onClick = {
                    val h = timePickerState.hour.toString().padStart(2, '0')
                    val m = timePickerState.minute.toString().padStart(2, '0')
                    mostrarReloj = false
                    onCambio(tipo, activoExterno, "$h:$m")
                }) { Text("Guardar") }
            },
            dismissButton    = { TextButton(onClick = { mostrarReloj = false }) { Text("Cancelar") } },
            text             = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Selecciona la hora de la misión",
                        style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}