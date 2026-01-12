package com.example.vita.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.vita.ui.AppStateViewModel
import com.example.vita.ui.components.BottomBar
import com.example.vita.ui.screens.CreateAcount.CreateAccountViewModel
import com.example.vita.ui.screens.CreateAcount.CreateAcountScreen
import com.example.vita.ui.screens.Game.GameScreen
import com.example.vita.ui.screens.Home.HomeScreen
import com.example.vita.ui.screens.Login.LoginScreen
import com.example.vita.ui.screens.Retos.RetosScreen
import com.example.vita.ui.screens.Login.LoginViewModel
import com.example.vita.ui.screens.Profile.ProfileScreen
import com.example.vita.ui.screens.Progress.ProgreScreen
import com.example.vita.ui.screens.Progress.ProgressScreen


// =================================================
//          1. NAVEGADOR RAÍZ (EL DIRECTOR DE ORQUESTA)
// =================================================
@Composable
fun AppNavigation(appStateViewModel: AppStateViewModel = viewModel()) {
    val isLoggedIn by appStateViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        MainNavigation()
    } else {
        AuthNavigation()
    }
}


// =================================================
//          2. NAVEGADOR DE AUTENTICACIÓN
// =================================================

@Composable
private fun AuthNavigation() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Routes.Auth.Login)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            // Pantalla de Login
            entry<Routes.Auth.Login> {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    onNavigateToCreateAccount = { backStack.add(Routes.Auth.CreateAccount) },
                    onLoginAttempt = { email, password ->
                        loginViewModel.signInWithEmailAndPassword(email, password) { success, error ->
                            if (!success) {
                                println("Error de inicio de sesión: $error")
                            }
                        }
                    }
                )
            }

            // Pantalla de Crear Cuenta
            entry<Routes.Auth.CreateAccount> {
                val createAccountViewModel: CreateAccountViewModel = viewModel()
                CreateAcountScreen(
                    onCreateAccountAttempt = { email, password ->
                        createAccountViewModel.crearCuenta(email, password) { success, error ->
                            if (!success) {
                                println("Error al crear cuenta: $error")
                            }
                        }
                    },
                    onNavigateBackToLogin = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}



// =================================================
// 3. NAVEGADOR PRINCIPAL (CON EL BOTTOM BAR)
// =================================================
@Composable
private fun MainNavigation(navController: NavHostController,
                           modifier: Modifier = Modifier
) {

    androidx.navigation.NavHost(
        navController = navController,
        startDestination = Routes.Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Retos.route) {
            RetoScreen()
        }
        composable(Screen.Juegos.route) {
            JuegosScreen()
        }
        composable(Screen.Progreso.route) {
            ProgresoScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }

    }



