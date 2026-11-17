package com.example.shuttwareapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shuttwareapp.R
import com.example.shuttwareapp.api.Red
import com.example.shuttwareapp.screens.HuecosScreen
import com.example.shuttwareapp.screens.LoginScreen
import com.example.shuttwareapp.screens.MisSesionesScreen
import com.example.shuttwareapp.screens.PerfilScreen
import com.example.shuttwareapp.screens.PreviewsScreen
import com.example.shuttwareapp.screens.RegisterScreen
import com.example.shuttwareapp.viewmodel.AuthViewModel
import com.example.shuttwareapp.viewmodel.HuecosViewModel
import com.example.shuttwareapp.viewmodel.PerfilViewModel
import com.example.shuttwareapp.viewmodel.SesionesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val apiService = Red.api

    val authViewModel: AuthViewModel = viewModel()
    val sesionesViewModel: SesionesViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val huecosViewModel: HuecosViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        modifier = modifier
    ) {

        //Se van llamando a los Composables de las distintas pantallas cargando en cada uno sus viewModel
        composable(Rutas.LOGIN) {
            LoginScreen(
                apiService = apiService,
                authViewModel = authViewModel,
                onLoginOk = {
                    val id = authViewModel.idUsuario
                    if (id != null) {
                        navController.navigate("sesiones/$id") {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onRegister = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegisterScreen(
                apiService = apiService,
                authViewModel = authViewModel,
                onRegisterOk = {
                    val id = authViewModel.idUsuario
                    if (id != null) {
                        navController.navigate("sesiones/$id") {
                            popUpTo(Rutas.REGISTRO) { inclusive = true }
                        }
                    }
                },
                onLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.SESIONES) { backStackEntry ->
            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")?.toIntOrNull() ?: 0

            MainScaffold(navController = navController, idUsuario = idUsuario) {
                MisSesionesScreen(
                    apiService = apiService,
                    idUsuario = idUsuario,
                    nombre = authViewModel.nombreUsuario ?: "usuario",
                    sesionesViewModel = sesionesViewModel,
                    onSesionClick = { idSesion ->
                        navController.navigate("previews/$idSesion")
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Rutas.HUECOS) { backStackEntry ->
            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")?.toIntOrNull() ?: 0


            MainScaffold(navController = navController, idUsuario = idUsuario) {
                HuecosScreen(
                    idUsuario = authViewModel.idUsuario ?: 0,
                    viewModel = huecosViewModel
                )
            }
        }

        composable(
            route = Rutas.PERFIL,
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: 0

            MainScaffold(navController = navController, idUsuario = idUsuario) {
                PerfilScreen(
                    apiService = apiService,
                    perfilViewModel = perfilViewModel,
                    idUsuario = idUsuario,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        //Pantalla para las previews de una sesion concreta
        composable(
            route = "previews/{idSesion}",
            arguments = listOf(navArgument("idSesion") { type = NavType.IntType })
        ) { backStackEntry ->
            val idSesion = backStackEntry.arguments?.getInt("idSesion") ?: return@composable

            //Busca la sesion ya cargada en el ViewModel
            val sesion = sesionesViewModel.sesiones.find { it.idSesion == idSesion }

            if (sesion != null) {
                PreviewsScreen(
                    sesion = sesion,
                    idUsuario = authViewModel.idUsuario ?: 0,
                    api = apiService,
                    onBack = { navController.popBackStack() }
                )
            } else {
                //Por si todavia no esta cargada
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.cargando_sesi_n))
                }
            }
        }
    }
}