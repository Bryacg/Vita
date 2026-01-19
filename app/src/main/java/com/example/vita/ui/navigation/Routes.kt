package com.example.vita.ui.navigation

sealed class Routes(val route: String) {
    // Rutas de Autenticación
    object AuthGraph : Routes("auth_graph")
    object Login : Routes("login")
    object CreateAccount : Routes("create_account")

    // Rutas Principales
    object MainGraph : Routes("main_graph")
    object Home : Routes("home")
    object Retos : Routes("retos")
    object Juegos : Routes("juegos")
    object Progreso : Routes("progreso")
    object Perfil : Routes("perfil")
}