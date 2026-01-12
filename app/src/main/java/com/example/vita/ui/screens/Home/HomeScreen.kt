package com.example.vita.ui.screens.Home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar={todoBar()},
        bottomBar = { navigatio() }
    ){innerPadding -> // 1. El contenido va en este bloque final (trailing lambda)

        // 2. Llama a tu composable 'contenido' aquí
        contenido(
            // 3. Pásale el padding que te da el Scaffold
            modifier = Modifier.padding(innerPadding)
        )

    }


}

fun navigatio() {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun todoBar() {
    TopAppBar(title = { Text("Vitagame") },)
}

@Composable
fun contenido(modifier: Modifier){
    Box(
        modifier = Modifier.fillMaxWidth().padding(6.dp)
    ){

    }
}
