package com.example.vita.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

//  Flujo de Autenticación: Contiene Login y Crear Cuenta
@Serializable
sealed class Routes : NavKey {

    @Serializable
    sealed class Auth : Routes() {
        @Serializable
        data object Login : Auth()

        @Serializable
        data object CreateAccount : Auth()
    }

    //  Flujo Principal: Contiene las pantallas post-login
    @Serializable
    sealed class Screen(val route: String) {
        object Home : Screen("home")
        object Retos : Screen("retos")
        object Juegos : Screen("juegos")
        object Progreso : Screen("progreso")
        object Profile : Screen("profile")
    }
}