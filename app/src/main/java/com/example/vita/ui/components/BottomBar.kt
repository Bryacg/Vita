package com.example.vita.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.vita.R
import com.example.vita.ui.navigation.Routes
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
fun BottomBar(backStack: NavBackStack<NavKey>) {
    val current = backStack.lastOrNull()

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = current == Routes.Main.Home,
            onClick = { backStack.add(Routes.Main.Home) },
            icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = current == Routes.Main.Retos,
            onClick = { backStack.add(Routes.Main.Retos) },
            icon = { Icon(painterResource(R.drawable.ic_challenges), contentDescription = "Retos") },
            label = { Text("Retos") }
        )
        NavigationBarItem(
            selected = current == Routes.Main.Game,
            onClick = { backStack.add(Routes.Main.Game) },
            icon = { Icon(painterResource(R.drawable.ic_game), contentDescription = "Game") },
            label = { Text("Juegos") }
        )
        NavigationBarItem(
            selected = current == Routes.Main.Progress,
            onClick = { backStack.add(Routes.Main.Progress) },
            icon = { Icon(painterResource(R.drawable.ic_progress), contentDescription = "Progress") },
            label = { Text("Progreso") }
        )
        NavigationBarItem(
            selected = current == Routes.Main.Perfil,
            onClick = { backStack.add(Routes.Main.Perfil) },
            icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
