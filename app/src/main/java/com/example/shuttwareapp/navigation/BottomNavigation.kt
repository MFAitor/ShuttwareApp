package com.example.shuttwareapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

//Barra de navegacion por la app
@Composable
fun MainScaffold(
    navController: NavHostController,
    idUsuario: Int,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        //Eliminacion de margenes automaticos
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                //Se eliminan los margenes de la barra
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                //Cada elemento permite navegar a su pantalla correspondiente
                NavigationBarItem(
                    //Seleccion de opcion
                    selected = currentRoute?.startsWith("sesiones") == true,
                    onClick = {
                        navController.navigate("sesiones/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Sesiones") },
                    label = { Text("Sesiones") }
                )

                NavigationBarItem(
                    selected = currentRoute?.startsWith("hueco") == true,
                    onClick = {
                        navController.navigate("huecos/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Hueco") },
                    label = { Text("Huecos") }
                )

                NavigationBarItem(
                    selected = currentRoute?.startsWith("perfil") == true,
                    onClick = {
                        navController.navigate("perfil/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        //Muestra el contenido principal respetando los márgenes de la barra
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}