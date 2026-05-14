package com.example.vita.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vita.ui.AppStateViewModel
import com.example.vita.ui.components.BottomBar
import com.example.vita.ui.screens.ChatBot.ChatBotFab
import com.example.vita.ui.screens.CreateAcount.CreateAccountScreen
import com.example.vita.ui.screens.CreateAcount.CreateAccountViewModel
import com.example.vita.ui.screens.Game.GameScreen
import com.example.vita.ui.screens.Home.HomeScreen
import com.example.vita.ui.screens.Login.LoginScreen
import com.example.vita.ui.screens.Login.LoginViewModel
import com.example.vita.ui.screens.Profile.ProfileScreen
import com.example.vita.ui.screens.Progress.ProgressScreen
import com.example.vita.ui.screens.Retos.RetosScreen
import com.example.vita.ui.screens.Retos.RetosViewModel

@Composable
fun AppNavigation(appStateViewModel: AppStateViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by appStateViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

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
        bottomBar = { if (showBottomBar) BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            NavHost(
                navController    = navController,
                startDestination = if (isLoggedIn == true) Routes.Home.route else Routes.Login.route,
                modifier         = Modifier.padding(innerPadding)
            ) {
                composable(Routes.Login.route) {
                    val loginViewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        viewModel                 = loginViewModel,
                        onNavigateToCreateAccount = { navController.navigate(Routes.CreateAccount.route) },
                        onLoginAttempt            = { email, password -> loginViewModel.login(email, password) },
                        onLoginSuccess            = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        },
                        // CORREGIDO: navegamos a Home PRIMERO, luego a Perfil encima.
                        // Así Home queda en el back stack y el BottomBar funciona.
                        onNavigateToProfile       = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                            navController.navigate(Routes.Perfil.route)
                        }
                    )
                }

                composable(Routes.CreateAccount.route) {
                    val vm: CreateAccountViewModel = hiltViewModel()
                    CreateAccountScreen(
                        viewModel              = vm,
                        onCreateAccountAttempt = { name, lastName, email, pass ->
                            vm.crearCuenta(name, lastName, email, pass)
                        },
                        onNavigateBackToLogin  = { navController.popBackStack() }
                    )
                }

                composable(Routes.Home.route) {
                    HomeScreen(navController = navController)
                }

                composable(Routes.Retos.route) {
                    val retosViewModel: RetosViewModel = hiltViewModel()
                    RetosScreen(viewModel = retosViewModel)
                }

                composable(Routes.Juegos.route)  { GameScreen() }
                composable(Routes.Progreso.route) { ProgressScreen() }

                composable(Routes.Perfil.route) {
                    ProfileScreen(
                        onLogoutSuccess    = {
                            navController.navigate(Routes.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        // CORREGIDO: cuando el perfil se guarda por primera vez,
                        // volvemos a Home que ya está en el back stack.
                        onPerfilCompletado = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Perfil.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            if (showBottomBar) {
                Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(bottom = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    ChatBotFab()
                }
            }
        }
    }
}