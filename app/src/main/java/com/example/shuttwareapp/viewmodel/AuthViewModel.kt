package com.example.shuttwareapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.api.ApiService
import kotlinx.coroutines.launch
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shuttwareapp.worker.HuecosWorker
import com.example.shuttwareapp.worker.PreviewsWorker
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    //Almacenamos el id y el nombre del usuario
    var idUsuario by mutableStateOf<Int?>(null)
        private set

    var nombreUsuario by mutableStateOf<String?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    //Comprobamos los datos e iniciamos sesion
    fun login(
        context: Context,
        apiService: ApiService,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        error = null

        //Comprobamos que los campos no esten vacios
        if (email.isBlank() || password.isBlank()) {
            error = "Debes introducir el email y la contraseña"
            return
        }

        viewModelScope.launch {
            try {
                //Enviamos los datos de acceso a la API
                val response = apiService.login(
                    mapOf(
                        "email" to email,
                        "contrasenia" to password
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val usuario = body?.get("usuario") as? Map<*, *>

                    //Recuperamos el id y el nombre del usuario
                    val id = (usuario?.get("id_usuario") as? Number)?.toInt()
                    val nombre = usuario?.get("nombre") as? String

                    if (id != null) {
                        idUsuario = id
                        nombreUsuario = nombre

                        //Guardamos el id para que puedan usarlo los Workers
                        val prefs = context.getSharedPreferences(
                            "shuttware",
                            Context.MODE_PRIVATE
                        )
                        prefs.edit().putInt("id_usuario", id).apply()

                        //Comprogbaciones periodicas para saltar notificaciones
                        programarComprobacionHuecos(context)
                        programarComprobacionPreviews(context)

                        onSuccess()

                    } else {
                        //La API ha respondido sin un usuario valido
                        error = "Datos de usuario incorrectos"
                    }

                } else {
                    //Error por parte del API
                    error = "Email o contraseña incorrectos"
                }

            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    //Programamos la comprobacion de nuevos huecos
    private fun programarComprobacionHuecos(context: Context) {
        val request = PeriodicWorkRequestBuilder<HuecosWorker>(
            15, TimeUnit.MINUTES
        ).build()

        //Registramos el trabajo periodico en WorkManager para saber cuando se abren nuevos huecos
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "comprobar_huecos",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    //Metodo para crear un usuario y entrar en su cuenta
    fun registro(
        context: Context,
        apiService: ApiService,
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        telefono: String?,
        instagram: String?,
        onSuccess: () -> Unit
    ) {
        error = null

        viewModelScope.launch {
            try {
                //Se preparan primero los datos obligatorios para mandar a la API
                val datos = mutableMapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "email" to email,
                    "contrasenia" to password
                )

                //Anadimos el telefono y el Instagram si tienen contenido
                if (!telefono.isNullOrBlank()) {
                    datos["telefono"] = telefono
                }

                if (!instagram.isNullOrBlank()) {
                    datos["url_instagram"] = instagram
                }

                //Enviamos los datos del registro a la API
                val response = apiService.registro(datos)

                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") as? Boolean ?: false

                    if (ok) {
                        //Se inicia la sesion automaticamente
                        login(context, apiService, email, password, onSuccess)
                    } else {
                        error = body?.get("mensaje") as? String ?: "No se pudo registrar"
                    }

                } else {
                    error = "Error del servidor"
                }

            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    //Programamos la comprobacion de nuevas previews
    private fun programarComprobacionPreviews(context: Context) {
        val request = PeriodicWorkRequestBuilder<PreviewsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        //Registramos el trabajo periodico en WorkManager para comprobar nuevas previews del ultimo usuario
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "comprobar_previews",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    //Cerrar la sesion y limpiamos los datos del usuario
    fun logout() {
        idUsuario = null
        nombreUsuario = null
        error = null
    }

    fun limpiarError() {
        error = null
    }
}