package com.example.vita.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
    val isLoggedIn    by appStateViewModel.isLoggedIn.collectAsState()

    // ── Splash mientras Firebase verifica la sesión ──────────────────────────
    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ── startDestination estable ─────────────────────────────────────────────
    // Se fija UNA VEZ cuando isLoggedIn deja de ser null.
    // No cambia en recomposiciones posteriores, evitando que el NavHost
    // resetee el back stack y cree HomeViewModel antes de que UserEntity
    // exista en Room (causa del FK constraint error 787).
    val startDestination = remember {
        if (isLoggedIn == true) Routes.Home.route else Routes.Login.route
    }

    // ── Auth guard ───────────────────────────────────────────────────────────
    // Cuando la sesión expira o el usuario cierra sesión desde cualquier
    // pantalla, forzamos la navegación al Login limpiando todo el back stack.
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // ── Bottom bar visibility ────────────────────────────────────────────────
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val mainRoutes = setOf(
        Routes.Home.route,
        Routes.Retos.route,
        Routes.Juegos.route,
        Routes.Progreso.route,
        Routes.Perfil.route
    )
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            NavHost(
                navController    = navController,
                startDestination = startDestination,
                modifier         = Modifier.padding(innerPadding)
            ) {

                // ── Login ────────────────────────────────────────────────────
                composable(Routes.Login.route) {
                    val loginViewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        viewModel                 = loginViewModel,
                        onNavigateToCreateAccount = {
                            navController.navigate(Routes.CreateAccount.route)
                        },
                        onLoginAttempt            = { email, password ->
                            loginViewModel.login(email, password)
                        },
                        onLoginSuccess            = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToProfile       = {
                            // Home queda en el back stack para que el BottomBar funcione
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                            navController.navigate(Routes.Perfil.route)
                        }
                    )
                }

                // ── Crear cuenta ─────────────────────────────────────────────
                composable(Routes.CreateAccount.route) {
                    val vm: CreateAccountViewModel = hiltViewModel()
                    CreateAccountScreen(
                        viewModel              = vm,
                        onCreateAccountAttempt = { name, lastName, email, pass ->
                            vm.crearCuenta(name, lastName, email, pass)
                        },
                        onNavigateBackToLogin  = {
                            navController.popBackStack()
                        },
                        // Registro exitoso, perfil ya configurado → Home
                        onNavigateToHome       = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        // Registro exitoso, usuario nuevo → Perfil (biometría)
                        // Home se empuja al stack primero para que el BottomBar
                        // funcione cuando el usuario complete el perfil
                        onNavigateToProfile    = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                            navController.navigate(Routes.Perfil.route)
                        }
                    )
                }

                // ── Pantallas principales (requieren sesión) ─────────────────
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
                            // El auth guard de LaunchedEffect también lo haría,
                            // pero navegamos explícitamente para respuesta inmediata
                            navController.navigate(Routes.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onPerfilCompletado = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Perfil.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // FAB del chatbot (solo en pantallas principales)
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