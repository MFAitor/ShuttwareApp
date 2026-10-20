package com.example.shuttwareapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shuttwareapp.screens.LoginScreen

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginCorrecto = {
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.INICIO) {
            // Pantalla temporal mientras hacemos las demás
            androidx.compose.material3.Text("Inicio")
        }
    }
}