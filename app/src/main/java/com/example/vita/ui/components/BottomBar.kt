package com.example.vita.ui.components
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vita.R
import com.example.vita.ui.navigation.Routes

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {

        // Item: HOME
        NavigationBarItem(
            selected = currentRoute == Routes.Home.route,
            onClick = {
                navController.navigate(Routes.Home.route) {

                    // Evita recargar la pantalla si ya estás en ella
                    launchSingleTop = true
                    // Restaura el estado si se navegó anteriormente
                    restoreState = true
                }
            },
            icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = "Home") },
            label = { Text("Home") }
        )

        // Item: RETOS
        NavigationBarItem(
            selected = currentRoute == Routes.Retos.route,
            onClick = {
                navController.navigate(Routes.Retos.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(painterResource(R.drawable.ic_challenges), contentDescription = "Retos") },
            label = { Text("Retos") }
        )

        // Item: JUEGOS
        NavigationBarItem(
            selected = currentRoute == Routes.Juegos.route,
            onClick = {
                navController.navigate(Routes.Juegos.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(painterResource(R.drawable.ic_game), contentDescription = "Juegos") },
            label = { Text("Juegos") }
        )

        // Item: PROGRESO
        NavigationBarItem(
            selected = currentRoute == Routes.Progreso.route,
            onClick = {
                navController.navigate(Routes.Progreso.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(painterResource(R.drawable.ic_progress), contentDescription = "Progreso") },
            label = { Text("Progreso") }
        )

        // Item: PERFIL
        NavigationBarItem(
            selected = currentRoute == Routes.Perfil.route,
            onClick = {
                navController.navigate(Routes.Perfil.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}