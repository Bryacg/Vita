package com.example.vita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compose.VitaTheme
import com.example.vita.ui.navigation.AppNavigation
import com.example.vita.ui.screens.Login.LoginScreen
import com.example.vita.ui.screens.Login.LoginViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VitaTheme {
//                LoginScreen(
//                    viewModel = LoginViewModel() ,
//                    onLoginSuccess = {},
//                   onNavigateToCreateAccount = {}
//                )

            }
        }
    }
}
