package com.example.vita.ui.screens.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vita.R
import com.example.vita.domain.model.User
import com.example.vita.ui.components.SwitchProfile


@Composable
fun ProfileScreen(){
    val usua = User("1","bchoez29@hotmail.com","Bryan Michael","Choez Giler",5,5225)
    var isExpanded by remember { mutableStateOf(false) }
    var isExpandedPerfil by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "¡usuario!")
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
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
            Column(){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),

                ){
                    Image(
                        painter = painterResource(id = R.drawable.logovt), // Reemplaza con tu ID
                        contentDescription = "Avatar del Usuario",
                        modifier = Modifier
                            .size(40.dp)// Mantengo el tamaño, pero ahora está contenido en su tercio
                            .weight(1f)// Un pequeño espacio para que no pegue con el texto
                    )
                    Column(modifier = Modifier.weight(7f)) {
                        Text("${usua.name}"+ " " + "${usua.lastName}")
                        Text(usua.email)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    , horizontalArrangement = Arrangement.Center){
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${usua.currentLevel}")
                        Text("Nivel")
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${usua.currentXp}")
                        Text("Puntos")
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("2")
                        Text("Racha")
                    }

                }
            }
        }

        Text(text = "¡Estadistica!")
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){

                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Desafiso Completados")
                    Text("10")
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Logros Desbloqueados")
                    Text("8")
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Experiencia Total")
                    Text("2500")
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Comida Registrada")
                    Text("8")
                }


            }
        }
        Text(text = "¡Logros!")
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){
                Text("Logro 1")
                Text("Logro 2")
                Text("Logro 3")
            }

        }
        Text(text = "¡Recordatori!")
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recordar Comida")
                    SwitchProfile()
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recordar Agua")
                    SwitchProfile()
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recordar Caminata")
                    SwitchProfile()
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Datos Usuario")
                    Text(">")
                }
                // CONTENIDO DESPLEGABLE (Solo se muestra si isExpanded es true)
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Aquí tus campos de edición (OutlinedTextField)
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Nombres") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Apellidos") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { isExpanded = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Guardar Cambios")
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpandedPerfil = !isExpandedPerfil },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Perfil Usuario")
                    Text(">")
                }
                if (isExpandedPerfil) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Aquí tus campos de edición (OutlinedTextField)
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Altura") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Peso") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Edad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Sexo") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { isExpandedPerfil = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Guardar Cambios")
                        }
                    }
                }

            }
        }

        Text(text = "Boton de Cierre de sesion")
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
    }
}