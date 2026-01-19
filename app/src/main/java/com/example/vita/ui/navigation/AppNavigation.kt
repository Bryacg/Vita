package com.example.vita.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vita.ui.AppStateViewModel
import com.example.vita.ui.components.BottomBar
import com.example.vita.ui.screens.CreateAcount.CreateAcountScreen
import com.example.vita.ui.screens.CreateAcount.CreateAccountViewModel
import com.example.vita.ui.screens.Game.GameScreen
import com.example.vita.ui.screens.Home.HomeScreen
import com.example.vita.ui.screens.Login.LoginScreen
import com.example.vita.ui.screens.Login.LoginViewModel
import com.example.vita.ui.screens.Profile.ProfileScreen
import com.example.vita.ui.screens.Progress.ProgressScreen
import com.example.vita.ui.screens.Retos.RetosScreen

@Composable
fun AppNavigation(appStateViewModel: AppStateViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by appStateViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.Home.route,
        Routes.Retos.route,
        Routes.Juegos.route,
        Routes.Progreso.route,
        Routes.Perfil.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Routes.Home.route else Routes.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- FLUJO DE AUTENTICACIÓN ---
            composable(Routes.Login.route) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    onNavigateToCreateAccount = {
                        navController.navigate(Routes.CreateAccount.route)
                    },
                    onLoginAttempt = { email, password ->
                        loginViewModel.login(email, password)
                    },
                    // AGREGAMOS ESTE PARÁMETRO QUE FALTABA
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            // Limpia el stack para que no pueda volver al Login con el botón "atrás"
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.CreateAccount.route) {
                val createAccountViewModel: CreateAccountViewModel = hiltViewModel()
                CreateAcountScreen(
                    onCreateAccountAttempt = { email, password ->
                        createAccountViewModel.crearCuenta(email, password)
                    },
                    onNavigateBackToLogin = { navController.popBackStack() }
                )
            }

            // --- FLUJO PRINCIPAL ---
            composable(Routes.Home.route) { HomeScreen() }
            composable(Routes.Retos.route) { RetosScreen() }
            composable(Routes.Juegos.route) { GameScreen() }
            composable(Routes.Progreso.route) { ProgressScreen() }
            composable(Routes.Perfil.route) { ProfileScreen() }
        }
    }
}