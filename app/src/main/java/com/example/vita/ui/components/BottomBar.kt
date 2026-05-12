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

        val tabs = listOf(
            Triple(Routes.Home.route,    R.drawable.ic_home,       "Home"),
            Triple(Routes.Retos.route,   R.drawable.ic_challenges, "Retos"),
            Triple(Routes.Juegos.route,  R.drawable.ic_game,       "Juegos"),
            Triple(Routes.Progreso.route,R.drawable.ic_progress,   "Progreso"),
            Triple(Routes.Perfil.route,  R.drawable.ic_profile,    "Perfil")
        )

        tabs.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = {
                    navController.navigate(route) {
                        // ✅ popUpTo + saveState/restoreState evita crear múltiples instancias
                        // del mismo destino y preserva el estado de cada pestaña
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon  = { Icon(painterResource(icon), contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}